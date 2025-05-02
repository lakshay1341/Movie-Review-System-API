package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// user entity - represents a person who can login and make reservations
@Entity
@Data // lombok ftw
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName; // login name

    @Column(nullable = false, unique = true)
    private String email; // for notifications and stuff

    @Column(nullable = false)
    private String password; // bcrypt encoded, never store plaintext!

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role; // ROLE_USER or ROLE_ADMIN

    // user can have many reservations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>(); // init empty list

    // user can write many reviews
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>(); // init empty list

    // todo: add profile pic url field later
}
