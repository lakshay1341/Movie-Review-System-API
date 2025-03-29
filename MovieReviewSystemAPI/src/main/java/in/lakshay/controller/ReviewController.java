package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.exception.ValidationException;
import in.lakshay.service.ReviewService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Management", description = "APIs for managing movie reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final MessageSource messageSource;

    @RateLimiter(name = "basic")
    @PostMapping("/movies/{movieId}")
    @Operation(summary = "Add new review", description = "Adds a new review for a specific movie")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Adding review for movie {} by user {}", movieId, username);
        
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
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> updates) {
        try {
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
}