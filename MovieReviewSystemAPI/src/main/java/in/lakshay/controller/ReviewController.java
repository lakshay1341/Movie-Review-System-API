package in.lakshay.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.lakshay.entity.Review;
import in.lakshay.service.ReviewService;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/movies/{movieId}")
    public ResponseEntity<Review> addReview(@PathVariable Long movieId, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(movieId, review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, Object> updates) {
        String comment = (String) updates.get("comment");
        int rating = (int) updates.get("rating");
        return ResponseEntity.ok(reviewService.updateReview(reviewId, comment, rating));
    }
}
