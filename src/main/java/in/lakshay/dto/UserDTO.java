package in.lakshay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * basic user info dto
 * used for profile and user mgmt screens
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;  // login name

    @Email(message = "Email should be valid")
    private String email;  // contact email

    private String roleName;  // ROLE_USER or ROLE_ADMIN
}
