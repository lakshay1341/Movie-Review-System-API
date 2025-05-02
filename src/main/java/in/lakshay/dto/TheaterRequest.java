package in.lakshay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// request for creating/updating theaters
@Data
public class TheaterRequest {
    @NotBlank(message = "Theater name is required") // gotta have a name
    private String name;  // theater name

    @NotBlank(message = "Theater location is required") // where is it?
    private String location;  // address

    @NotNull(message = "Theater capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1") // cant have 0 capacity lol
    private Integer capacity;  // how many seats total

    // TODO: maybe add multiple screens/halls later?
}
