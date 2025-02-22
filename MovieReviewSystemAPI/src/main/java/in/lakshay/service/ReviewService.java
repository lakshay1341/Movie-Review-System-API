package in.lakshay.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.lakshay.entity.Movie;
import in.lakshay.entity.Review;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.ReviewRepository;


@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MovieRepository movieRepository;

    public Review addReview(Long movieId, Review review) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        review.setMovie(movie);
        return reviewRepository.save(review);
    }

    public Review updateReview(Long reviewId, String comment, int rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setComment(comment);
        review.setRating(rating);
        return reviewRepository.save(review);
    }
}
