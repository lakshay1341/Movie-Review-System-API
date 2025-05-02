package in.lakshay.dto;

import lombok.Data;

// login form data
@Data
public class LoginRequest {
    private String username;  // user login name
    private String password;  // plaintext pw (will be hashed)

    // note: never log passwords!
}