package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// stripe checkout session info
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSessionDTO {
    private String sessionId;   // stripe session id
    private String sessionUrl;  // redirect url for payment

    // note: never log the full session details - sensitive data!
}
