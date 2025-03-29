package in.lakshay.test;

import in.lakshay.dto.ReviewDTO;
import in.lakshay.dto.ReviewRequest;
import in.lakshay.entity.Movie;
import in.lakshay.entity.Review;
import in.lakshay.entity.User;
import in.lakshay.repo.MovieRepository;
import in.lakshay.repo.ReviewRepository;
import in.lakshay.repo.UserRepository;
import in.lakshay.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieRepository movieRepository;
    
    @InjectMocks
    private ReviewService reviewService;

    @Test
    void addReview_ValidInput_ReturnsReviewDTO() {
        // Arrange
        String username = "testUser";
        Long movieId = 1L;
        ReviewRequest request = new ReviewRequest();
        request.setComment("Great movie!");
        request.setRating(5);

        User user = new User();
        user.setUserName(username);
        
        Movie movie = new Movie();
        movie.setId(movieId);
        
        Review review = new Review();
        review.setId(1L); // Set an ID for the saved review
        review.setUser(user);
        review.setMovie(movie);
        review.setComment(request.getComment());
        review.setRating(request.getRating());

        when(userRepository.findByUserName(username)).thenReturn(user);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review)); // Add this line

        // Act
        ReviewDTO result = reviewService.addReview(username, movieId, request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getComment(), result.getComment());
        assertEquals(request.getRating(), result.getRating());
        assertEquals(movieId, result.getMovieId());
        assertEquals(username, result.getUserName());
        
        // Verify interactions
        verify(userRepository).findByUserName(username);
        verify(movieRepository).findById(movieId);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).findById(review.getId());
    }
}