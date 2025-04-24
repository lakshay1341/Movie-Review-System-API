package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Success URL is required")
    @Pattern(regexp = "^https?://.*", message = "Success URL must be a valid URL")
    private String successUrl;

    @NotNull(message = "Cancel URL is required")
    @Pattern(regexp = "^https?://.*", message = "Cancel URL must be a valid URL")
    private String cancelUrl;
}
