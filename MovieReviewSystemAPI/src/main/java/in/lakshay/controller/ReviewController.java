package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addReview(@PathVariable Long movieId, @RequestBody ReviewRequest reviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReviewDTO savedReview = reviewService.addReview(authentication.getName(), movieId, reviewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true,
                "review.added.success",
                savedReview
        ));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> updates) {
        ReviewDTO updatedReview = reviewService.updateReview(
                reviewId,
                (String) updates.get("comment"),
                (int) updates.get("rating"),
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "review.updated.success",
                updatedReview
        ));
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMyReviews() {
        List<ReviewDTO> reviews = reviewService.getReviewsByUser(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "review.user.retrieved.success",
                reviews
        ));
    }
}