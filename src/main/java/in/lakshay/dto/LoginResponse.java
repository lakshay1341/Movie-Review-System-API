package in.lakshay.dto;

import lombok.*;

// response after successful login
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;  // JWT auth token

    // TODO: maybe add expiration time?
}