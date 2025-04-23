package in.lakshay.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    private String title;
    private String genre;
    private int releaseYear;
    private String description;
    private String posterImageUrl;
    private List<ReviewDTO> reviews;
}