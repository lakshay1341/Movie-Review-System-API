package in.lakshay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TheaterRequest {
    @NotBlank(message = "Theater name is required")
    private String name;
    
    @NotBlank(message = "Theater location is required")
    private String location;
    
    @NotNull(message = "Theater capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
