package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserBlockRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Size(max = 255, message = "Reason must be less than 255 characters")
    private String reason;
}
