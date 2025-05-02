package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * extended user info for admin panel
 * includes activity stats
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    // basic user info
    private Long id;
    private String userName;
    private String email;
    private String roleName;  // ROLE_USER or ROLE_ADMIN

    // activity metrics
    private int reservationCount;  // how many bookings
    private int reviewCount;       // how many reviews

    // TODO: maybe add last login date?
}
