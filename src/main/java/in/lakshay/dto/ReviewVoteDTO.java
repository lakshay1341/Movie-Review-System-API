package in.lakshay.dto;

import lombok.Data;

import java.time.LocalDateTime;

// vote on a review (helpful/not helpful)
@Data
public class ReviewVoteDTO {
    private Long id;  // pk
    private Long reviewId;  // which review
    private String userName;  // who voted
    private boolean isUpvote;  // thumbs up or down
    private LocalDateTime votedAt;  // when

    // TODO: maybe add ability to change vote?
}
