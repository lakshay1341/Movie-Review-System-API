package in.lakshay.dto;

import lombok.*;

import java.util.List;

// movie data for frontend
@Data // lombok ftw
@AllArgsConstructor
@NoArgsConstructor // jackson needs this
public class MovieDTO {
    private Long id;
    private String title;
    private String genre; // comma-sep for multiple genres
    private int releaseYear; // just year (2023 etc)
    private String description; // plot summary etc
    private String posterImageUrl; // s3 bucket url

    private List<ReviewDTO> reviews; // null if no reviews yet
}