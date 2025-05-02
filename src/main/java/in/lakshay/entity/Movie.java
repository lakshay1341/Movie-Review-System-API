package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// movie entity - represents a film in our system
@Entity
@Data  // lombok magic for getters/setters
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(nullable = false) // title is required
    private String title;

    @Column(nullable = false)
    private String genre; // maybe should be enum? too late now

    @Column(name = "release_year", nullable = false)
    private int releaseYear; // just the year, not full date

    @Column(columnDefinition = "TEXT") // for longer descriptions
    private String description;

    @Column(name = "poster_image_url") // s3 url usually
    private String posterImageUrl;  // stores the url to the movie poster

    // bidirectional relationship with reviews
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true) // delete reviews when movie deleted
    private List<Review> reviews = new ArrayList<>();  // init empty list


    // movie can have many showtimes
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Showtime> showtimes = new ArrayList<>(); // init to avoid NPE

    // todo: add avg rating calculation method
}