package in.lakshay.dto;


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
    private Integer statusId;
    private String statusValue;
    private Double totalPrice;
    private boolean paid;
}
