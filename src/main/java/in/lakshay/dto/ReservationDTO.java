package in.lakshay.dto;

import in.lakshay.entity.Reservation.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private String userName;
    private Long showtimeId;
    private String movieTitle;
    private String theaterName;
    private String showDate;
    private String showTime;
    private List<SeatDTO> seats;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private Double totalPrice;
}
