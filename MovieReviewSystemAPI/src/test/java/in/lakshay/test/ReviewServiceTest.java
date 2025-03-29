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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Movie movie = new Movie();
        Review review = new Review();

        when(userRepository.findByUserName(username)).thenReturn(user);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(reviewRepository.save(any())).thenReturn(review);

        // Act
        ReviewDTO result = reviewService.addReview(username, movieId, request);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).save(any());
    }
}