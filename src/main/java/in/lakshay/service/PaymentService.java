package in.lakshay.service;

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
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /**
     * Creates a Stripe Checkout Session for a reservation
     */
    @Transactional
    public CheckoutSessionDTO createCheckoutSession(Long reservationId, String successUrl, String cancelUrl) throws StripeException {
        log.info("Creating checkout session for reservation: {}", reservationId);

        // Initialize Stripe with API key
        Stripe.apiKey = stripeApiKey;

        // Find the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        // Check if payment already exists and is successful
        Optional<Payment> existingPayment = paymentRepository.findByReservation(reservation);
        if (existingPayment.isPresent() && existingPayment.get().getStatus() == Payment.PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException("Payment already completed for this reservation");
        }

        // Create line item description
        String description = String.format("Movie Reservation #%d - %s",
                reservation.getId(),
                reservation.getShowtime().getMovie().getTitle());

        // Create a checkout session with Stripe
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(reservation.getId().toString())
                .setCustomerEmail(reservation.getUser().getEmail())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount((long) (reservation.getTotalPrice() * 100)) // Amount in cents
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Movie Reservation")
                                        .setDescription(description)
                                        .build())
                                .build())
                        .setQuantity(1L)
                        .build())
                .build();

        Session session = Session.create(params);

        // Save or update payment record
        Payment payment = existingPayment.orElse(new Payment());
        payment.setReservation(reservation);
        payment.setPaymentIntentId(session.getId());
        payment.setAmount(reservation.getTotalPrice());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Return session details
        return new CheckoutSessionDTO(session.getId(), session.getUrl());
    }

    /**
     * Handles Stripe webhook events
     */
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) throws StripeException {
        log.info("Handling Stripe webhook event");

        // Verify the webhook signature
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        // Get the event type
        String eventType = event.getType();
        log.info("Received Stripe event: {}", eventType);

        // Process the event based on its type
        if (eventType.equals("checkout.session.completed")) {
            handleCheckoutSessionCompleted(payload);
        } else if (eventType.equals("payment_intent.succeeded")) {
            handlePaymentIntentSucceeded(payload);
        } else if (eventType.equals("charge.succeeded")) {
            handleChargeSucceeded(payload);
        } else {
            log.info("Unhandled event type: {}", eventType);
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

    /**
     * Updates payment status to SUCCEEDED and marks reservation as paid
     */
    @Transactional
    public void updateReservationStatus(Reservation reservation) {
        if (reservation == null) {
            log.warn("Cannot update status for null reservation");
            return;
        }

        Long reservationId = reservation.getId();
        log.info("Marking reservation as paid for ID: {}", reservationId);

        try {
            // Get a fresh copy of the reservation from the database
            Reservation freshReservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

            // Mark the reservation as paid and update status to PAID (2)
            freshReservation.setPaid(true);
            freshReservation.setStatusId(2); // 2 = PAID
            reservationRepository.save(freshReservation);

            // Find the payment for this reservation
            Payment payment = paymentRepository.findByReservation(reservation).orElse(null);

            if (payment != null) {
                // Update payment status to SUCCEEDED
                payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                log.info("Payment status updated to SUCCEEDED and reservation marked as paid for ID: {}", reservationId);
            } else {
                log.warn("No payment found for reservation ID: {}", reservationId);
            }
        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e);
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

    /**
     * Maps Payment entity to PaymentDTO
     */
    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        paymentDTO.setReservationId(payment.getReservation().getId());
        return paymentDTO;
    }
}
