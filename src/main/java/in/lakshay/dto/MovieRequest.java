package in.lakshay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

// request obj for creating/updating movies
@Data
public class MovieRequest {
    @NotBlank(message = "Title is required")
    private String title;  // movie name

    @NotBlank(message = "Genre is required")
    private String genre;  // comma-sep genres

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Release year must be after 1888") // first movie ever
    @Max(value = 2030, message = "Release year must be before 2030") // future limit
    private Integer releaseYear;  // just the year

    @Size(max = 5000, message = "Description must be less than 5000 characters") // no novels plz
    private String description;  // plot summary

    private String posterImageUrl;  // s3 url for poster

    // TODO: maybe add director/actors later?
}