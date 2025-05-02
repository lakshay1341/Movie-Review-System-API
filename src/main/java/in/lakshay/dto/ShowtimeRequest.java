package in.lakshay.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

// request obj for creating/updating showtimes
@Data
public class ShowtimeRequest {
    @NotNull(message = "Movie ID is required")
    private Long movieId;  // which movie

    @NotNull(message = "Theater ID is required")
    private Long theaterId;  // which theater

    @NotNull(message = "Show date is required")
    @Future(message = "Show date must be in the future") // duh
    private LocalDate showDate;  // when

    @NotNull(message = "Show time is required")
    private LocalTime showTime;  // what time

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1") // cant have 0 seats lol
    private Integer totalSeats;  // capacity

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative") // no negative prices!
    private Double price;  // ticket cost
}
