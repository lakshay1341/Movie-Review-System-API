package in.lakshay.controller;

import com.stripe.exception.StripeException;
import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.PaymentDTO;
import in.lakshay.dto.CheckoutSessionDTO;
import in.lakshay.dto.PaymentRequest;
import in.lakshay.service.PaymentService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/create-checkout-session")
    @PreAuthorize("isAuthenticated()")
    @RateLimiter(name = "basic")
    @Operation(summary = "Create checkout session", description = "Creates a Stripe checkout session for a reservation")
    public ResponseEntity<ApiResponse<CheckoutSessionDTO>> createCheckoutSession(
            @Valid @RequestBody PaymentRequest paymentRequest) {
        
        log.info("Creating checkout session for reservation: {}", paymentRequest.getReservationId());
        
        try {
            // Client should provide complete URLs for success and cancel redirects
            String successUrl = paymentRequest.getSuccessUrl();
            String cancelUrl = paymentRequest.getCancelUrl();
            
            // Validate URLs
            if (successUrl == null || cancelUrl == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(
                        false,
                        "Success and cancel URLs are required",
                        null
                ));
            }
            
            CheckoutSessionDTO checkoutSessionDTO = paymentService.createCheckoutSession(
                    paymentRequest.getReservationId(), successUrl, cancelUrl);
            
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("payment.session.created", null, LocaleContextHolder.getLocale()),
                    checkoutSessionDTO
            ));
        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Stripe error: " + e.getMessage(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            messageSource.getMessage("payment.session.error", null, LocaleContextHolder.getLocale()),
                            null
                    ));
        }
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("isAuthenticated()")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get payment by reservation", description = "Returns payment details for a reservation")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByReservation(@PathVariable Long reservationId) {
        log.info("Getting payment for reservation: {}", reservationId);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            // Authorization check would be done in service layer
            
            PaymentDTO paymentDTO = paymentService.getPaymentByReservationId(reservationId);
            
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("payment.retrieved", null, LocaleContextHolder.getLocale()),
                    paymentDTO
            ));
        } catch (Exception e) {
            log.error("Error retrieving payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PostMapping("/webhook")
    @Operation(summary = "Stripe webhook", description = "Handles Stripe webhook events")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request, @RequestBody String payload) {
        log.info("Received Stripe webhook");
        
        String sigHeader = request.getHeader("Stripe-Signature");
        log.debug("Stripe-Signature header: {}", sigHeader);
        
        if (sigHeader == null) {
            log.error("Missing Stripe-Signature header");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Stripe-Signature header");
        }
        
        try {
            paymentService.handleWebhookEvent(payload, sigHeader);
            log.info("Webhook processed successfully");
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (StripeException e) {
            log.error("Stripe webhook error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing error");
        }
    }
}
