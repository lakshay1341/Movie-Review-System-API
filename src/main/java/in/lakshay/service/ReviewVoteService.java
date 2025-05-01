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
     */
    @Transactional
    public ReviewVoteDTO voteReview(Long reviewId, String username, boolean isUpvote) {
        log.info("User {} voting on review {}, isUpvote: {}", username, reviewId, isUpvote);
        
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // Check if user has already voted on this review
        Optional<ReviewVote> existingVote = reviewVoteRepository.findByReviewAndUser(review, user);
        
        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();
            
            // If the vote type is the same, remove the vote (toggle)
            if (vote.isUpvote() == isUpvote) {
                reviewVoteRepository.delete(vote);
                
                // Update review vote counts
                updateReviewVoteCounts(review);
                
                return null; // Vote removed
            } else {
                // Change vote type
                vote.setUpvote(isUpvote);
                ReviewVote savedVote = reviewVoteRepository.save(vote);
                
                // Update review vote counts
                updateReviewVoteCounts(review);
                
                return mapToDTO(savedVote);
            }
        } else {
            // Create new vote
            ReviewVote vote = new ReviewVote();
            vote.setReview(review);
            vote.setUser(user);
            vote.setUpvote(isUpvote);
            
            ReviewVote savedVote = reviewVoteRepository.save(vote);
            
            // Update review vote counts
            updateReviewVoteCounts(review);
            
            return mapToDTO(savedVote);
        }
    }
    
    /**
     * Get all votes for a review
     */
    public List<ReviewVoteDTO> getVotesForReview(Long reviewId) {
        log.info("Getting votes for review {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        List<ReviewVote> votes = reviewVoteRepository.findByReview(review);
        
        return votes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a user has voted on a review
     */
    public boolean hasUserVotedOnReview(Long reviewId, String username) {
        log.info("Checking if user {} has voted on review {}", username, reviewId);
        
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        return reviewVoteRepository.findByReviewAndUser(review, user).isPresent();
    }
    
    /**
     * Get user's vote on a review
     */
    public ReviewVoteDTO getUserVoteOnReview(Long reviewId, String username) {
        log.info("Getting user {} vote on review {}", username, reviewId);
        
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        Optional<ReviewVote> vote = reviewVoteRepository.findByReviewAndUser(review, user);
        
        return vote.map(this::mapToDTO).orElse(null);
    }
    
    /**
     * Update review vote counts
     */
    @Transactional
    public void updateReviewVoteCounts(Review review) {
        int upvotes = reviewVoteRepository.countUpvotesByReview(review);
        int downvotes = reviewVoteRepository.countDownvotesByReview(review);
        
        review.setUpvotes(upvotes);
        review.setDownvotes(downvotes);
        
        reviewRepository.save(review);
    }
    
    /**
     * Map ReviewVote entity to DTO
     */
    private ReviewVoteDTO mapToDTO(ReviewVote vote) {
        ReviewVoteDTO dto = new ReviewVoteDTO();
        dto.setId(vote.getId());
        dto.setReviewId(vote.getReview().getId());
        dto.setUserName(vote.getUser().getUserName());
        dto.setUpvote(vote.isUpvote());
        dto.setVotedAt(vote.getVotedAt());
        return dto;
    }
}
