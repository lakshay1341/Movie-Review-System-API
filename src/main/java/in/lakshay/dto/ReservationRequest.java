package in.lakshay.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservationRequest {
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;
}
