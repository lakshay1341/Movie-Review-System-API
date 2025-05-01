package in.lakshay.repo;

import in.lakshay.entity.Movie;
import in.lakshay.entity.Review;
import in.lakshay.entity.Review.ReviewStatus;
import in.lakshay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);

    List<Review> findByMovie(Movie movie);

    List<Review> findByMovieAndStatus(Movie movie, ReviewStatus status);

    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.movie.id = ?1 AND r.status = ?2")
    List<Review> findByMovieIdAndStatus(Long movieId, ReviewStatus status);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = ?1 AND r.status = 'APPROVED'")
    Double getAverageRatingForMovie(Long movieId);
}