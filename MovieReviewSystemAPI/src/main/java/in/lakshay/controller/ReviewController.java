package in.lakshay.controller;

import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ReviewDTO> addReview(@PathVariable Long movieId, @RequestBody ReviewRequest reviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} adding review for movie ID {}", username, movieId);
        ReviewDTO savedReview = reviewService.addReview(username, movieId, reviewRequest);
        return ResponseEntity.ok(savedReview);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> updates) {
        String comment = (String) updates.get("comment");
        int rating = (int) updates.get("rating");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User {} updating review ID {}", username, reviewId);
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, comment, rating, username);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public List<ReviewDTO> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Fetching reviews for user {}", username);
        return reviewService.getReviewsByUser(username);
    }
}