package in.lakshay.dto;

import in.lakshay.entity.Review.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

// movie review data
@Data
public class ReviewDTO {
    private Long id;
    private String comment;      // review text
    private int rating;          // 1-5 stars

    // movie info
    private Long movieId;
    private String movieTitle;

    // user info
    private String userName;     // who wrote it
    private Long userId;         // user id

    // timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // voting stuff
    private Integer upvotes;     // thumbs up count
    private Integer downvotes;   // thumbs down count
    private String helpfulTags;  // comma-sep tags

    // status & current user's vote
    private ReviewStatus status; // APPROVED, PENDING, etc
    private boolean userHasVoted;       // has current user voted
    private boolean userVoteIsUpvote;   // was it an upvote

    // frontend needs username not userName (sigh)
    public String getUsername() {
        return userName; // just return the same thing
    }
}
