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
    private List<ReviewDTO> reviews;
}