package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// component type for master data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentTypeDTO {
    private Integer id;  // pk
    private String name;  // eg: RESERVATION_STATUS, PAYMENT_STATUS

    // used to categorize master data items
}
