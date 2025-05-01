package in.lakshay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for basic User information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @Email(message = "Email should be valid")
    private String email;

    private String roleName;
}
