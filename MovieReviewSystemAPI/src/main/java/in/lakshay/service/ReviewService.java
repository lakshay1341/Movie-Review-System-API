package in.lakshay.service;

import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.entity.Movie;
import in.lakshay.entity.Review;
import in.lakshay.entity.User;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.ReviewRepository;
import in.lakshay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    public ReviewDTO addReview(String username, Long movieId, ReviewRequest reviewRequest) {
        log.info("Adding review for movie {} by user {}", movieId, username);
        try {
            User user = userRepository.findByUserName(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

            Review review = new Review();
            review.setUser(user);
            review.setMovie(movie);
            review.setComment(reviewRequest.getComment());
            review.setRating(reviewRequest.getRating());

            // Verify relationships before saving
            if (review.getMovie() == null || review.getUser() == null) {
                log.error("Failed to set movie or user relationship");
                throw new RuntimeException("Failed to create review: invalid movie or user relationship");
            }

            Review savedReview = reviewRepository.save(review);
            log.info("Review successfully added with ID: {}", savedReview.getId());
            
            // Refresh the entity to ensure all relationships are loaded
            savedReview = reviewRepository.findById(savedReview.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved review"));
            
            return mapToDTO(savedReview);
        } catch (Exception e) {
            log.error("Error adding review: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ReviewDTO updateReview(Long reviewId, String comment, int rating, String username) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUser().getUserName().equals(username) && !hasRole(username, "ROLE_ADMIN")) {
            throw new RuntimeException("Not authorized to update this review");
        }
        review.setComment(comment);
        review.setRating(rating);
        Review updatedReview = reviewRepository.save(review);
        return mapToDTO(updatedReview);
    }

    public List<ReviewDTO> getReviewsByUser(String username) {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public boolean isReviewOwner(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        return review != null && review.getUser().getUserName().equals(username);
    }

    private boolean hasRole(String username, String roleName) {
        User user = userRepository.findByUserName(username);
        return user != null && user.getRole().getName().equals(roleName);
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        
        // Add null checks for Movie
        if (review.getMovie() != null) {
            dto.setMovieId(review.getMovie().getId());
            dto.setMovieTitle(review.getMovie().getTitle());
        }
        
        // Add null check for User
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getUserName());
        }
        
        return dto;
    }
}
