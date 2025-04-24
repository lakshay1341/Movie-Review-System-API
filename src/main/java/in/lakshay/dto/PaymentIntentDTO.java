package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentDTO {
    private String clientSecret;
    private String publicKey;
}
