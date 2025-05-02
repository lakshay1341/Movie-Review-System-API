package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// stripe payment intent info
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentDTO {
    private String clientSecret;  // from stripe api
    private String publicKey;     // our public key

    // note: never log the client secret! sensitive data
}
