package in.lakshay.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private int rating;
    private Long movieId;
    private String movieTitle;
    private String userName;
}