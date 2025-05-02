package in.lakshay.dto;

import lombok.Data;
import in.lakshay.util.Constants;
import jakarta.validation.constraints.*;

// request for creating/updating reviews
@Data
public class ReviewRequest {
    @NotBlank(message = "Comment cannot be empty") // gotta say something
    @Size(max = Constants.MAX_COMMENT_LENGTH, message = "Comment too long") // no essays plz
    private String comment;  // review text

    @Min(value = Constants.MIN_RATING, message = "Rating must be between 1 and 5") // min 1 star
    @Max(value = Constants.MAX_RATING, message = "Rating must be between 1 and 5") // max 5 stars
    private int rating;  // star rating

    @Size(max = 255, message = "Helpful tags must be less than 255 characters") // reasonable limit
    private String helpfulTags;  // comma-sep tags like "funny,insightful"

    // note: user info comes from auth context, not request
}