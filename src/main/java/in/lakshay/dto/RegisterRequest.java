package in.lakshay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// signup form data
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required") // cant be empty
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") // reasonable length
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens") // no weird chars
    private String username;  // login name

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address") // basic format check
    private String email;  // contact email

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters") // min security
    private String password;  // will be hashed w/ bcrypt

    // note: never log passwords!
}