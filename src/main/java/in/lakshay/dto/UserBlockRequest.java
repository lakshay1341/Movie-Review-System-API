package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// request to block a user (admin function)
@Data
public class UserBlockRequest {
    @NotNull(message = "User ID is required") // gotta know who to block
    private Long userId;  // who to block

    @Size(max = 255, message = "Reason must be less than 255 characters") // keep it brief
    private String reason;  // why they're blocked

    // blocker info comes from auth context
}
