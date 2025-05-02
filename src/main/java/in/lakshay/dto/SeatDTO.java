package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// seat info for theater seating
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDTO {
    private Long id;  // pk
    private Long showtimeId;  // which showing
    private String seatNumber;  // like "A1", "B5" etc
    private Boolean isReserved;  // taken or not

    // TODO: maybe add price per seat later?
}
