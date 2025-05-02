package in.lakshay.repo;

import in.lakshay.entity.Review;
import in.lakshay.entity.ReviewVote;
import in.lakshay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// handles upvotes/downvotes on reviews
@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    // check if user already voted on a review
    Optional<ReviewVote> findByReviewAndUser(Review review, User user); // prevents double voting

    // get all votes for a review
    List<ReviewVote> findByReview(Review review);

    // get all votes by a user (across all reviews)
    List<ReviewVote> findByUser(User user); // for user profile

    // count upvotes for a review
    @Query("SELECT COUNT(rv) FROM ReviewVote rv WHERE rv.review = ?1 AND rv.isUpvote = true")
    int countUpvotesByReview(Review review); // thumbs up count

    // count downvotes for a review
    @Query("SELECT COUNT(rv) FROM ReviewVote rv WHERE rv.review = ?1 AND rv.isUpvote = false")
    int countDownvotesByReview(Review review); // thumbs down count

    // remove a user's vote on a review (if they change their mind)
    void deleteByReviewAndUser(Review review, User user); // for vote removal
}
