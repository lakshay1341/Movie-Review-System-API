package in.lakshay.repo;

import in.lakshay.entity.Movie;
import in.lakshay.entity.Showtime;
import in.lakshay.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// repo for managing showtimes
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    // find showtimes by date
    List<Showtime> findByShowDate(LocalDate date);

    // get all showtimes for a movie
    List<Showtime> findByMovie(Movie movie);

    // get all showtimes at a theater
    List<Showtime> findByTheater(Theater theater);

    // find by date and movie combo
    List<Showtime> findByShowDateAndMovie(LocalDate date, Movie movie);

    // find by date and theater
    List<Showtime> findByShowDateAndTheater(LocalDate date, Theater theater);

    // find available showtimes from a date
    @Query("SELECT s FROM Showtime s WHERE s.showDate >= :date AND s.availableSeats > 0")
    Page<Showtime> findAvailableShowtimesFromDate(LocalDate date, Pageable pageable);

    // find available showtimes for a specific movie from date
    // todo: maybe add theater filter here too?
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.showDate >= :date AND s.availableSeats > 0")
    Page<Showtime> findAvailableShowtimesForMovieFromDate(Long movieId, LocalDate date, Pageable pageable);
}
