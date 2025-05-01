package in.lakshay.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewVoteDTO {
    private Long id;
    private Long reviewId;
    private String userName;
    private boolean isUpvote;
    private LocalDateTime votedAt;
}
