package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// represents a physical theater location
@Entity
@Data
@Table(name = "theaters")
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // eg: PVR, INOX, etc

    @Column(nullable = false)
    private String location; // address of the theater

    @Column(nullable = false)
    private Integer capacity; // total seats in theater

    // one theater can have many showtimes
    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<Showtime> showtimes = new ArrayList<>(); // init empty list

    // todo: add method to check if theater is at capacity
}
