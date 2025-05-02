package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.dto.ReviewVoteDTO;
import in.lakshay.dto.ReviewVoteRequest;
import in.lakshay.entity.Review.ReviewStatus;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.exception.ValidationException;
import in.lakshay.service.ReviewService;
import in.lakshay.service.ReviewVoteService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews") // base path for review endpoints
@Tag(name = "Review Management", description = "APIs for managing movie reviews") // swagger docs
@Slf4j // logging
public class ReviewController {
    @Autowired // TODO: switch to constructor injection
    private ReviewService reviewService; // handles review business logic

    @Autowired
    private ReviewVoteService reviewVoteService; // handles upvotes/downvotes

    @Autowired
    private MessageSource messageSource; // i18n

    @RateLimiter(name = "basic") // prevent abuse
    @PostMapping("/movies/{movieId}") // add review for a movie
    @Operation(summary = "Add new review", description = "Adds a new review for a specific movie")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // must be logged in
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequest reviewRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Adding review for movie {} by user {}", movieId, username);

        // this will create a review in PENDING status for admin approval
        ReviewDTO review = reviewService.addReview(username, movieId, reviewRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        messageSource.getMessage(
                                "review.added.success",
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        review
                ));
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}") // update existing review
    @PreAuthorize("hasRole('ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)") // admin or owner only
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> updates) {
        try {
            // extract update fields from request
            // not using a DTO here cuz we only need 2 fields
            ReviewDTO updatedReview = reviewService.updateReview(
                    reviewId,
                    (String) updates.get("comment"),
                    (int) updates.get("rating"),
                    SecurityContextHolder.getContext().getAuthentication().getName()
            );
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "review.updated.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    updatedReview
            ));
        } catch (ValidationException e) {
            // validation failed - return errors
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            messageSource.getMessage(
                                    "validation.failed",
                                    null,
                                    LocaleContextHolder.getLocale()
                            ),
                            e.getErrors()
                    ));
        }
    }

    @RateLimiter(name = "basic")
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getMyReviews() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.user.retrieved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                reviews
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/movies/{movieId}")
    @Operation(summary = "Get reviews for a movie", description = "Returns all approved reviews for a specific movie")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByMovie(@PathVariable Long movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ReviewDTO> reviews = reviewService.getReviewsByMovie(movieId, username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.retrieved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                reviews
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/pending") // reviews waiting for approval
    @PreAuthorize("hasRole('ADMIN')") // admin only
    @Operation(summary = "Get pending reviews", description = "Returns all reviews pending moderation (Admin only)")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getPendingReviews(
            @PageableDefault(size = 10) Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Admin {} fetching pending reviews", username); // track admin activity

        // get reviews waiting for approval
        Page<ReviewDTO> reviews = reviewService.getPendingReviews(pageable, username);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.pending.retrieved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                reviews
        ));
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a review", description = "Approves a pending review (Admin only)")
    public ResponseEntity<ApiResponse<ReviewDTO>> approveReview(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO review = reviewService.approveReview(reviewId, username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.approved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                review
        ));
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject a review", description = "Rejects a pending review (Admin only)")
    public ResponseEntity<ApiResponse<ReviewDTO>> rejectReview(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO review = reviewService.rejectReview(reviewId, username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.rejected.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                review
        ));
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}/helpful-tags")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update helpful tags", description = "Updates the helpful tags for a review (Admin only)")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateHelpfulTags(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> request) {

        String helpfulTags = request.get("helpfulTags");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ReviewDTO review = reviewService.updateHelpfulTags(reviewId, helpfulTags, username);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "review.tags.updated.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                review
        ));
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}/upvote") // thumbs up
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Upvote a review", description = "Upvotes a review or removes the upvote if already upvoted")
    public ResponseEntity<ApiResponse<ReviewVoteDTO>> upvoteReview(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} upvoting review {}", username, reviewId);

        try {
            // true = upvote (thumbs up)
            ReviewVoteDTO vote = reviewVoteService.voteReview(reviewId, username, true);

            // If vote is null, it means the vote was removed (toggle)
            // clicking upvote twice removes it
            if (vote == null) {
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        messageSource.getMessage(
                                "review.upvote.removed.success",
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "review.upvote.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    vote
            ));
        } catch (ResourceNotFoundException e) {
            // review not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (AccessDeniedException e) {
            // user not allowed to vote on this review
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            // something else went wrong
            log.error("Error upvoting review {}: {}", reviewId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @RateLimiter(name = "basic")
    @PutMapping("/{reviewId}/downvote") // thumbs down
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Downvote a review", description = "Downvotes a review or removes the downvote if already downvoted")
    public ResponseEntity<ApiResponse<ReviewVoteDTO>> downvoteReview(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} downvoting review {}", username, reviewId);

        try {
            // false = downvote (thumbs down)
            ReviewVoteDTO vote = reviewVoteService.voteReview(reviewId, username, false);

            // If vote is null, it means the vote was removed (toggle)
            // clicking downvote twice removes it
            if (vote == null) {
                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        messageSource.getMessage(
                                "review.downvote.removed.success",
                                null,
                                LocaleContextHolder.getLocale()
                        ),
                        null
                ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "review.downvote.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    vote
            ));
        } catch (ResourceNotFoundException e) {
            // review not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (AccessDeniedException e) {
            // user not allowed to vote on this review
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            // something else went wrong
            log.error("Error downvoting review {}: {}", reviewId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }
} // end of ReviewController