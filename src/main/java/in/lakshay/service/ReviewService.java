package in.lakshay.service;

import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.entity.Movie;
import in.lakshay.entity.Review;
import in.lakshay.entity.Review.ReviewStatus;
import in.lakshay.entity.ReviewVote;
import in.lakshay.entity.User;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.ReviewRepository;
import in.lakshay.repo.ReviewVoteRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewVoteRepository reviewVoteRepository;

    @Autowired
    private UserBlockService userBlockService;

    /**
     * Add a new review from user
     */
    @Transactional
    public ReviewDTO addReview(String username, Long movieId, ReviewRequest reviewRequest) { // main review creation method
        log.info("Adding review for movie {} by user {}", movieId, username);
        try {
            User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

            Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

            // Check if user is blocked by admin
            if (userBlockService.isUserBlockedByAdmin(user.getId())) {
                throw new AccessDeniedException("You are blocked by an admin and cannot post reviews");
            }

            Review review = new Review();
            review.setUser(user);
            review.setMovie(movie);
            review.setComment(reviewRequest.getComment());
            review.setRating(reviewRequest.getRating());

            // Set review status based on moderation settings
            // For now, we'll approve all reviews automatically
            // TODO: implement proper moderation workflow later
            review.setStatus(ReviewStatus.APPROVED); // auto-approve for now

            // Set helpful tags if provided
            if (reviewRequest.getHelpfulTags() != null && !reviewRequest.getHelpfulTags().isEmpty()) {
                review.setHelpfulTags(reviewRequest.getHelpfulTags());
            }

            // Verify relationships before saving
            if (review.getMovie() == null || review.getUser() == null) {
                log.error("Failed to set movie or user relationship");
                throw new RuntimeException("Failed to create review: invalid movie or user relationship");
            }

            Review savedReview = reviewRepository.save(review);
            log.info("Review successfully added with ID: {}", savedReview.getId());

            // Refresh the entity to ensure all relationships are loaded
            // this is kinda redundant but hibernate can be weird sometimes
            savedReview = reviewRepository.findById(savedReview.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Failed to retrieve saved review"));

            return mapToDTO(savedReview, username);
        } catch (Exception e) {
            log.error("Error adding review: {}", e.getMessage(), e);
            throw e;
        }
    }

    // lets users edit their reviews
    @Transactional
    public ReviewDTO updateReview(Long reviewId, String comment, int rating, String username) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Check if user is authorized to update this review
        if (!review.getUser().getUserName().equals(username) && !hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Not authorized to update this review");
        }

        // Check if user is blocked by admin
        if (userBlockService.isUserBlockedByAdmin(review.getUser().getId())) {
            throw new AccessDeniedException("This user is blocked by an admin and cannot update reviews");
        }

        review.setComment(comment);
        review.setRating(rating);

        // If an admin is updating someone else's review, reset the status to PENDING for re-moderation
        // this is a bit weird but makes sense i guess
        if (!review.getUser().getUserName().equals(username) && hasRole(username, "ROLE_ADMIN")) {
            review.setStatus(ReviewStatus.PENDING);
        }

        Review updatedReview = reviewRepository.save(review);
        return mapToDTO(updatedReview, username);
    }

    // fetch all reviews by a specific user
    public List<ReviewDTO> getReviewsByUser(String username) {
        String currentUsername = username; // The currently authenticated user - needed for vote status

        User user = userRepository.findByUserName(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream().map(review -> mapToDTO(review, currentUsername)).collect(Collectors.toList());
    }

    /*
     * get all reviews for a specific movie
     * admins see all reviews, users only see approved ones
     */
    public List<ReviewDTO> getReviewsByMovie(Long movieId, String username) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        // Only return approved reviews for regular users
        List<Review> reviews;
        if (hasRole(username, "ROLE_ADMIN")) {
            reviews = reviewRepository.findByMovie(movie);
        } else {
            reviews = reviewRepository.findByMovieAndStatus(movie, ReviewStatus.APPROVED);
        }

        return reviews.stream().map(review -> mapToDTO(review, username)).collect(Collectors.toList());
    }

    // admin-only: get reviews waiting for approval
    public Page<ReviewDTO> getPendingReviews(Pageable pageable, String username) {
        if (!hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can access pending reviews");
        }

        Page<Review> pendingReviews = reviewRepository.findByStatus(ReviewStatus.PENDING, pageable);
        return pendingReviews.map(review -> mapToDTO(review, username));
    }

    // admin hits the approve button
    @Transactional
    public ReviewDTO approveReview(Long reviewId, String username) {
        if (!hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can approve reviews");
        }

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setStatus(ReviewStatus.APPROVED);
        Review updatedReview = reviewRepository.save(review);

        return mapToDTO(updatedReview, username);
    }

    // admin says no to a review
    @Transactional
    public ReviewDTO rejectReview(Long reviewId, String username) {
        if (!hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can reject reviews");
        }

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setStatus(ReviewStatus.REJECTED);
        Review updatedReview = reviewRepository.save(review);

        return mapToDTO(updatedReview, username);
    }

    // admin can add tags like "helpful", "insightful" etc
    @Transactional
    public ReviewDTO updateHelpfulTags(Long reviewId, String helpfulTags, String username) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Only admins can update helpful tags
        if (!hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can update helpful tags");
        }

        review.setHelpfulTags(helpfulTags);
        Review updatedReview = reviewRepository.save(review);

        return mapToDTO(updatedReview, username);
    }

    // quick check if user owns the review
    public boolean isReviewOwner(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        return review != null && review.getUser().getUserName().equals(username);
    }

    // helper method - checks user roles
    private boolean hasRole(String username, String roleName) {
        return userRepository.findByUserName(username)
            .map(user -> user.getRole().getName().equals(roleName))
            .orElse(false);
    }

    // calc avg rating - used on movie details page
    public Double getAverageRatingForMovie(Long movieId) {
        return reviewRepository.getAverageRatingForMovie(movieId);
    }

    // convert db entity to dto for frontend
    private ReviewDTO mapToDTO(Review review, String currentUsername) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setUpvotes(review.getUpvotes());
        dto.setDownvotes(review.getDownvotes());
        dto.setHelpfulTags(review.getHelpfulTags());
        dto.setStatus(review.getStatus());

        // Add null checks for Movie
        if (review.getMovie() != null) {
            dto.setMovieId(review.getMovie().getId());
            dto.setMovieTitle(review.getMovie().getTitle());
        }

        // Add null check for User and ensure username is always set
        if (review.getUser() != null) {
            // Set the username from the User entity
            String userName = review.getUser().getUserName();
            log.info("Setting username in ReviewDTO: {} for review ID: {}", userName, review.getId());
            dto.setUserName(userName);

            // Also set the userId for reference
            Long userId = review.getUser().getId();
            // this log is probably excessive but whatever
            log.info("Setting userId in ReviewDTO: {} for review ID: {}", userId, review.getId());
            dto.setUserId(userId);
        } else {
            log.warn("User is null for review ID: {}", review.getId());
        }

        // Check if the current user has voted on this review
        if (currentUsername != null && !currentUsername.isEmpty()) {
            try {
                User currentUser = userRepository.findByUserName(currentUsername)
                    .orElse(null);

                if (currentUser != null) {
                    Optional<ReviewVote> vote = reviewVoteRepository.findByReviewAndUser(review, currentUser);
                    dto.setUserHasVoted(vote.isPresent());
                    dto.setUserVoteIsUpvote(vote.isPresent() && vote.get().isUpvote());
                }
            } catch (Exception e) {
                log.error("Error checking if user has voted: {}", e.getMessage());
                // Don't fail the whole operation if this check fails
                // just assume they haven't voted - not the end of the world
                dto.setUserHasVoted(false);
                dto.setUserVoteIsUpvote(false); // obvs false if they haven't voted
            }
        }

        return dto;
    }
}
