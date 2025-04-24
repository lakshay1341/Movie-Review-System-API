package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "status_id", nullable = false)
    private Integer statusId = 1; // Default to CONFIRMED (1)

    @Column(nullable = false)
    private boolean paid = false;

    @Transient
    private String statusValue; // This will be populated from master data

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;


}
