package in.lakshay.service;

import com.itextpdf.text.DocumentException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import in.lakshay.dto.CheckoutSessionDTO;
import in.lakshay.dto.PaymentDTO;
import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.PaymentRepository;
import in.lakshay.repo.ReservationRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional; // for nullable results

// handles all payment processing with Stripe integration

@Service
@Slf4j // logging
public class PaymentService {

    @Autowired // todo: switch to constructor injection someday
    private PaymentRepository paymentRepository; // db stuff

    @Autowired
    private ReservationRepository reservationRepository; // for finding reservations

    @Autowired
    private ModelMapper modelMapper; // entity <-> dto mapper

    @Autowired
    private PdfService pdfService; // makes the pdf receipts

    @Autowired
    private EmailService emailService; // sends the emails

    @Value("${stripe.api.key}") // from application.properties
    private String stripeApiKey; // stripe secret key - don't log this!

    @Value("${stripe.webhook.secret}")
    private String webhookSecret; // for verifying stripe callbacks

    // creates a stripe checkout session so user can pay
    @Transactional
    public CheckoutSessionDTO createCheckoutSession(Long reservationId, String successUrl, String cancelUrl) throws StripeException {
        log.info("Creating checkout session for reservation: {}", reservationId);

        // gotta set the API key for each request
        Stripe.apiKey = stripeApiKey; // from props

        // Find the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        // Make sure they haven't already paid
        Optional<Payment> existingPayment = paymentRepository.findByReservation(reservation);
        if (existingPayment.isPresent() && existingPayment.get().getStatus() == Payment.PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException("Payment already completed for this reservation"); // no double payments!
        }

        // what shows on checkout page
        String description = String.format("Movie Reservation #%d - %s",
                reservation.getId(),
                reservation.getShowtime().getMovie().getTitle());

        // stripe has a crazy builder pattern API
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT) // one-time payment
                .setSuccessUrl(successUrl) // redirect after payment
                .setCancelUrl(cancelUrl) // if they cancel
                .setClientReferenceId(reservation.getId().toString()) // so we can find it later
                .setCustomerEmail(reservation.getUser().getEmail()) // prefill their email
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd") // TODO: support more currencies someday
                                .setUnitAmount((long) (reservation.getTotalPrice() * 100)) // stripe wants cents
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Movie Reservation")
                                        .setDescription(description) // movie title + res id
                                        .build())
                                .build())
                        .setQuantity(1L) // just one reservation
                        .build())
                .build();

        // call the stripe API
        Session session = Session.create(params);

        // Save payment record in our db
        Payment payment = existingPayment.orElse(new Payment());
        payment.setReservation(reservation);
        payment.setPaymentIntentId(session.getId());
        payment.setAmount(reservation.getTotalPrice());
        payment.setStatus(Payment.PaymentStatus.PENDING); // not paid yet
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Return the session info to frontend
        return new CheckoutSessionDTO(session.getId(), session.getUrl());
    }

    // handles incoming webhook events from stripe
    // this is how we know when payments succeed
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) throws StripeException {
        log.info("Handling Stripe webhook event");

        // Verify the webhook signature - prevents forgery
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        // Get the event type - stripe sends many different event types
        String eventType = event.getType();
        log.info("Received Stripe event: {}", eventType);

        // Process the event based on its type - we only care about a few
        if (eventType.equals("checkout.session.completed")) { // user completed checkout
            handleCheckoutSessionCompleted(payload);
        } else if (eventType.equals("payment_intent.succeeded")) { // payment succeeded
            handlePaymentIntentSucceeded(payload);
        } else if (eventType.equals("charge.succeeded")) { // charge went through
            handleChargeSucceeded(payload);
        } else {
            log.info("Unhandled event type: {}", eventType); // we ignore other events
        }
    }

    /**
     * Handles checkout.session.completed event
     */
    private void handleCheckoutSessionCompleted(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject object = data.getJSONObject("object");

            String sessionId = object.getString("id");
            String clientReferenceId = object.optString("client_reference_id", null);

            log.info("Processing checkout.session.completed for session: {}, client reference: {}",
                    sessionId, clientReferenceId);

            // Find payment by session ID
            Payment payment = paymentRepository.findByPaymentIntentId(sessionId).orElse(null);

            // If payment not found but we have client reference ID, try to find by reservation ID
            if (payment == null && clientReferenceId != null && !clientReferenceId.isEmpty()) {
                try {
                    Long reservationId = Long.parseLong(clientReferenceId);
                    Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

                    if (reservation != null) {
                        // Create new payment record
                        payment = new Payment();
                        payment.setReservation(reservation);
                        payment.setPaymentIntentId(sessionId);
                        payment.setAmount(reservation.getTotalPrice());
                        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                        payment.setCreatedAt(LocalDateTime.now());
                        payment.setUpdatedAt(LocalDateTime.now());

                        paymentRepository.save(payment);

                        // Update reservation status
                        updateReservationStatus(reservation);
                        return;
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid client reference ID: {}", clientReferenceId);
                }
            }

            // If payment found, update it
            if (payment != null) {
                payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                // Update reservation status
                updateReservationStatus(payment.getReservation());
            }
        } catch (Exception e) {
            log.error("Error processing checkout.session.completed event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles payment_intent.succeeded event
     */
    private void handlePaymentIntentSucceeded(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject object = data.getJSONObject("object");

            String paymentIntentId = object.getString("id");

            log.info("Processing payment_intent.succeeded for payment intent: {}", paymentIntentId);

            // Find payments that might be associated with this payment intent
            // This is a fallback mechanism in case the checkout.session.completed event fails
            paymentRepository.findAll().forEach(payment -> {
                if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
                    payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    // Update reservation status
                    updateReservationStatus(payment.getReservation());
                }
            });
        } catch (Exception e) {
            log.error("Error processing payment_intent.succeeded event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles charge.succeeded event
     */
    private void handleChargeSucceeded(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject object = data.getJSONObject("object");

            String paymentIntentId = object.optString("payment_intent", null);

            if (paymentIntentId != null && !paymentIntentId.isEmpty()) {
                log.info("Processing charge.succeeded for payment intent: {}", paymentIntentId);

                // Find payment by payment intent ID
                Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId).orElse(null);

                if (payment != null) {
                    payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                    payment.setUpdatedAt(LocalDateTime.now());

                    // Try to get receipt URL
                    if (object.has("receipt_url") && !object.isNull("receipt_url")) {
                        payment.setReceiptUrl(object.getString("receipt_url"));
                    }

                    paymentRepository.save(payment);

                    // Update reservation status
                    updateReservationStatus(payment.getReservation());
                }
            }
        } catch (Exception e) {
            log.error("Error processing charge.succeeded event: {}", e.getMessage(), e);
        }
    }

    // updates payment status to SUCCEEDED and marks reservation as paid
    // called when we get confirmation from stripe
    @Transactional
    public void updateReservationStatus(Reservation reservation) {
        if (reservation == null) {
            log.warn("Cannot update status for null reservation"); // sanity check
            return;
        }

        Long reservationId = reservation.getId();
        log.info("Marking reservation as paid for ID: {}", reservationId);

        try {
            // Get a fresh copy of the reservation from the database
            // this avoids any stale data issues
            Reservation freshReservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

            // Mark the reservation as paid and update status to PAID (2)
            freshReservation.setPaid(true); // boolean flag
            freshReservation.setStatusId(2); // 2 = PAID in master_data
            reservationRepository.save(freshReservation);

            // Find the payment for this reservation
            Payment payment = paymentRepository.findByReservation(reservation).orElse(null);

            if (payment != null) {
                // Update payment status to SUCCEEDED
                payment.setStatus(Payment.PaymentStatus.SUCCEEDED); // enum value
                payment.setUpdatedAt(LocalDateTime.now()); // timestamp the update
                paymentRepository.save(payment);

                // Generate PDF receipt and send email to user
                generateAndSendReceipt(payment); // async operation

                log.info("Payment status updated to SUCCEEDED and reservation marked as paid for ID: {}", reservationId);
            } else {
                log.warn("No payment found for reservation ID: {}", reservationId); // shouldn't happen
            }
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e); // log full stack trace
        }
    }

    /**
     * Gets payment details by reservation ID
     */
    public PaymentDTO getPaymentByReservationId(Long reservationId) {
        log.info("Getting payment for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for reservation: " + reservationId));

        return mapToDTO(payment);
    }

    // convert payment entity to DTO
    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        paymentDTO.setReservationId(payment.getReservation().getId());
        return paymentDTO;
    }

    // makes a PDF receipt and emails it to customer
    // called after payment is confirmed
    @Transactional
    public void generateAndSendReceipt(Payment payment) {
        log.info("Generating and sending receipt for payment ID: {}", payment.getId());

        try {
            // make the PDF
            String pdfPath = pdfService.generateReceipt(payment); // gives us file path

            // save the path in db
            payment.setPdfReceiptPath(pdfPath);
            paymentRepository.save(payment);

            // email it to customer
            emailService.sendReceiptEmail(payment, pdfPath); // might fail

            log.info("Receipt generated and sent successfully for payment ID: {}", payment.getId());
        } catch (DocumentException e) {
            log.error("Error generating PDF receipt: {}", e.getMessage(), e); // iText problem
        } catch (MessagingException e) {
            log.error("Error sending receipt email: {}", e.getMessage(), e); // email problem
        } catch (Exception e) {
            log.error("Unexpected error in receipt generation/sending: {}", e.getMessage(), e); // something else broke
        }
    }
}
