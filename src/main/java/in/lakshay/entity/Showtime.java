package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// represents a specific movie showing at a specific theater
@Entity
@Data
@Table(name = "showtimes")
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;  // which movie is showing

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;  // where it's showing

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;  // when it's showing (date)

    @Column(name = "show_time", nullable = false)
    private LocalTime showTime;  // when it's showing (time)

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;  // how many seats total

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;  // how many seats left

    @Column(name = "price", nullable = false)
    private Double price;  // ticket price

    // all seats for this showtime
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>(); // init empty list

    // all reservations for this showtime
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>(); // init empty list

    // todo: add method to check if showtime is sold out
    // todo: add method to calculate revenue
}
