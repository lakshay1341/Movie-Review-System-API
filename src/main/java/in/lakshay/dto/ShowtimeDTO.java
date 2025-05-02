package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

// dto for showtime stuff - has movie and theater info
@Data // lombok does the magic
@AllArgsConstructor // all args constructor
@NoArgsConstructor // jackson needs this
public class ShowtimeDTO {
    private Long id; // pk
    private Long movieId; // FK to movie table
    private String movieTitle; // denormalized cuz its convenient
    private String moviePosterUrl; // for the UI to show

    private Long theaterId; // FK to theater
    private String theaterName; // denormalized
    private String theaterLocation; // where it is

    // show timing stuff
    private LocalDate showDate; // date part
    private LocalTime showTime; // time part

    // seats
    private Integer totalSeats; // how many total
    private Integer availableSeats; // how many left

    // todo: maybe add currency code later?
    private Double price; // in USD
}
