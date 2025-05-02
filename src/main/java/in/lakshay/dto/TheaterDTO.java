package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// basic theater info
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheaterDTO {
    private Long id;
    private String name;  // theater name
    private String location;  // address
    private Integer capacity;  // max ppl

    // TODO: maybe add screens/halls later?
}
