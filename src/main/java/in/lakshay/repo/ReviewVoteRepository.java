package in.lakshay.repo;

import in.lakshay.entity.Review;
import in.lakshay.entity.ReviewVote;
import in.lakshay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    Optional<ReviewVote> findByReviewAndUser(Review review, User user);
    
    List<ReviewVote> findByReview(Review review);
    
    List<ReviewVote> findByUser(User user);
    
    @Query("SELECT COUNT(rv) FROM ReviewVote rv WHERE rv.review = ?1 AND rv.isUpvote = true")
    int countUpvotesByReview(Review review);
    
    @Query("SELECT COUNT(rv) FROM ReviewVote rv WHERE rv.review = ?1 AND rv.isUpvote = false")
    int countDownvotesByReview(Review review);
    
    void deleteByReviewAndUser(Review review, User user);
}
