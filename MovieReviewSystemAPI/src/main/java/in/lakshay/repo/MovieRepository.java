package in.lakshay.repo;

import in.lakshay.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Movie> findByTitleOrGenreContainingIgnoreCase(String search, Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews")
    Page<Movie> findAllWithReviews(Pageable pageable);
}