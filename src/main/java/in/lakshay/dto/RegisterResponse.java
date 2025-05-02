package in.lakshay.dto;

import lombok.*;

// response after successful registration
@Data
@AllArgsConstructor
public class RegisterResponse {
    private String username;  // echo back the username

    // TODO: maybe add userId or token later?
}