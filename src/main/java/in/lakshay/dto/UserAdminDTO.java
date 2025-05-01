package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User information with additional details for admin views
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    private Long id;
    private String userName;
    private String email;
    private String roleName;
    private int reservationCount;
    private int reviewCount;
}
