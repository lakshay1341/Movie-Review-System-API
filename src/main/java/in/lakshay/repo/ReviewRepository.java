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

// handles movie review data access
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // get all reviews by a user
    List<Review> findByUser(User user);

    // get all reviews for a movie (regardless of status)
    List<Review> findByMovie(Movie movie);

    // get reviews for a movie with specific status (PENDING, APPROVED, REJECTED)
    List<Review> findByMovieAndStatus(Movie movie, ReviewStatus status);

    // get all reviews with a specific status (for admin moderation)
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    // same as above but using movie ID directly
    @Query("SELECT r FROM Review r WHERE r.movie.id = ?1 AND r.status = ?2")
    List<Review> findByMovieIdAndStatus(Long movieId, ReviewStatus status);

    // calc avg rating for a movie - only counts approved reviews!
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = ?1 AND r.status = 'APPROVED'")
    Double getAverageRatingForMovie(Long movieId); // used for movie cards display
}