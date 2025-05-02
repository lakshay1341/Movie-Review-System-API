package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// payment request for stripe checkout
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;  // which reservation to pay for

    @NotNull(message = "Success URL is required")
    @Pattern(regexp = "^https?://.*", message = "Success URL must be a valid URL") // url validation
    private String successUrl;  // redirect after success

    @NotNull(message = "Cancel URL is required")
    @Pattern(regexp = "^https?://.*", message = "Cancel URL must be a valid URL") // url validation
    private String cancelUrl;   // redirect if cancelled

    // TODO: maybe add payment method type later?
}
