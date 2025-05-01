package in.lakshay.dto;

import in.lakshay.entity.Review.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private int rating;
    private Long movieId;
    private String movieTitle;
    private String userName;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer upvotes;
    private Integer downvotes;
    private String helpfulTags;
    private ReviewStatus status;
    private boolean userHasVoted;
    private boolean userVoteIsUpvote;
    
    // Add username field for frontend compatibility
    public String getUsername() {
        return userName;
    }
}
