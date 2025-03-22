package in.lakshay.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String comment;
    private int rating;
}