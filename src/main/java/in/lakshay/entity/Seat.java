package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

// represents a single seat in a theater for a specific showtime
@Entity
@Data
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;  // which showtime this seat belongs to

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;  // like "A1", "B5", etc

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved = false;  // default not reserved

    @ManyToOne
    @JoinColumn(name = "reservation_id")  // nullable cuz seat might not be reserved
    private Reservation reservation;  // which reservation this seat belongs to (if any)

    // todo: maybe add seat type (premium, regular) later?
}
