package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSessionDTO {
    private String sessionId;
    private String sessionUrl;
}
