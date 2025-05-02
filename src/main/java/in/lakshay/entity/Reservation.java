package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// represents a booking made by a user for a specific showtime
@Entity
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // who made the reservation

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;  // for which showtime

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();  // which seats were reserved

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;  // when the reservation was made

    @Column(name = "status_id", nullable = false)
    private Integer statusId = 1; // Default to CONFIRMED (1) - others: CANCELLED (2), PENDING (3)

    @Column(nullable = false)
    private boolean paid = false;  // has payment been made?

    @Transient // not stored in db
    private String statusValue; // This will be populated from master data

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;  // total cost of all seats

    // todo: add method to calculate total price based on seats
    // todo: add method to check if reservation is valid
}
