package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewVoteRequest {
    @NotNull(message = "Vote type is required")
    private Boolean isUpvote;
}
