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

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByShowDate(LocalDate date);
    
    List<Showtime> findByMovie(Movie movie);
    
    List<Showtime> findByTheater(Theater theater);
    
    List<Showtime> findByShowDateAndMovie(LocalDate date, Movie movie);
    
    List<Showtime> findByShowDateAndTheater(LocalDate date, Theater theater);
    
    @Query("SELECT s FROM Showtime s WHERE s.showDate >= :date AND s.availableSeats > 0")
    Page<Showtime> findAvailableShowtimesFromDate(LocalDate date, Pageable pageable);
    
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.showDate >= :date AND s.availableSeats > 0")
    Page<Showtime> findAvailableShowtimesForMovieFromDate(Long movieId, LocalDate date, Pageable pageable);
}
