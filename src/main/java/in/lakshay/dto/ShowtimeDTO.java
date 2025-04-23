package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Long theaterId;
    private String theaterName;
    private String theaterLocation;
    private LocalDate showDate;
    private LocalTime showTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double price;
}
