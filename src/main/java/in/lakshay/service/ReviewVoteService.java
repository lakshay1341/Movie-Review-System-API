package in.lakshay.service;

import in.lakshay.dto.ReviewVoteDTO;
import in.lakshay.entity.Review;
import in.lakshay.entity.ReviewVote;
import in.lakshay.entity.User;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.ReviewRepository;
import in.lakshay.repo.ReviewVoteRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewVoteService {

    @Autowired
    private ReviewVoteRepository reviewVoteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Vote on a review (upvote or downvote)
     * handles both creating new votes and changing existing ones
     */
    @Transactional
    public ReviewVoteDTO voteReview(Long reviewId, String username, boolean isUpvote) {
        log.info("User {} voting on review {}, isUpvote: {}", username, reviewId, isUpvote); // track who's voting

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Check if user has already voted on this review
        // need to handle this case specially
        Optional<ReviewVote> existingVote = reviewVoteRepository.findByReviewAndUser(review, user);

        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();

            // If the vote type is the same, remove the vote (toggle)
            // this lets users undo their votes by clicking again
            if (vote.isUpvote() == isUpvote) {
                reviewVoteRepository.delete(vote);

                // Update review vote counts
                updateReviewVoteCounts(review); // recalc the totals

                return null; // Vote removed - frontend handles this
            } else {
                // Change vote type - user switched from upvote to downvote or vice versa
                vote.setUpvote(isUpvote); // flip it!
                ReviewVote savedVote = reviewVoteRepository.save(vote);

                // Update review vote counts
                updateReviewVoteCounts(review); // important!

                return mapToDTO(savedVote);
            }
        } else {
            // Create new vote - first time this user is voting on this review
            ReviewVote vote = new ReviewVote(); // fresh obj
            vote.setReview(review);
            vote.setUser(user);
            vote.setUpvote(isUpvote); // true=upvote, false=downvote

            ReviewVote savedVote = reviewVoteRepository.save(vote);

            // Update review vote counts
            updateReviewVoteCounts(review);

            return mapToDTO(savedVote);
        }
    }

    // fetch all votes for a specific review
    // used on admin screens
    public List<ReviewVoteDTO> getVotesForReview(Long reviewId) {
        log.info("Getting votes for review {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        List<ReviewVote> votes = reviewVoteRepository.findByReview(review);

        return votes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // quick boolean check if user voted already
    public boolean hasUserVotedOnReview(Long reviewId, String username) {
        log.info("Checking if user {} has voted on review {}", username, reviewId);

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        return reviewVoteRepository.findByReviewAndUser(review, user).isPresent();
    }

    // get details about user's vote if any
    // returns null if they haven't voted
    public ReviewVoteDTO getUserVoteOnReview(Long reviewId, String username) {
        log.info("Getting user {} vote on review {}", username, reviewId);

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        Optional<ReviewVote> vote = reviewVoteRepository.findByReviewAndUser(review, user);

        return vote.map(this::mapToDTO).orElse(null);
    }

    // recalculate and update the vote counts on a review
    // this runs after any vote changes
    @Transactional
    public void updateReviewVoteCounts(Review review) {
        // count from scratch to be safe
        int upvotes = reviewVoteRepository.countUpvotesByReview(review);
        int downvotes = reviewVoteRepository.countDownvotesByReview(review);

        // update the review obj
        review.setUpvotes(upvotes);
        review.setDownvotes(downvotes);

        // save it back to db
        reviewRepository.save(review);
    }

    // convert db entity to dto for api response
    private ReviewVoteDTO mapToDTO(ReviewVote vote) { // standard mapper
        ReviewVoteDTO dto = new ReviewVoteDTO();
        dto.setId(vote.getId());
        dto.setReviewId(vote.getReview().getId());
        dto.setUserName(vote.getUser().getUserName());
        dto.setUpvote(vote.isUpvote());
        dto.setVotedAt(vote.getVotedAt());
        return dto;
    }
}
