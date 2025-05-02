package in.lakshay.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

// request for booking tickets
@Data
public class ReservationRequest {
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;  // which showing

    @NotEmpty(message = "At least one seat must be selected") // cant book 0 seats!
    private List<Long> seatIds;  // which seats to reserve

    // note: user comes from auth context
    // price calculated on server side
}
