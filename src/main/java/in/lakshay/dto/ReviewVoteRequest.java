package in.lakshay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// request to vote on a review
@Data
public class ReviewVoteRequest {
    @NotNull(message = "Vote type is required") // must specify up or down
    private Boolean isUpvote;  // true=thumbs up, false=thumbs down

    // user comes from auth context
    // review id comes from path param
}
