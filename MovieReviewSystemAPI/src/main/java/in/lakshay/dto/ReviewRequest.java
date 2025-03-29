package in.lakshay.dto;

import lombok.Data;
import in.lakshay.util.Constants;
import jakarta.validation.constraints.*;

@Data
public class ReviewRequest {
    @NotBlank(message = "Comment cannot be empty")
    @Size(max = Constants.MAX_COMMENT_LENGTH, message = "Comment too long")
    private String comment;

    @Min(value = Constants.MIN_RATING, message = "Rating must be between 1 and 5")
    @Max(value = Constants.MAX_RATING, message = "Rating must be between 1 and 5")
    private int rating;
}