package in.lakshay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// lookup data for dropdowns etc
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataDTO {
    private Integer id;  // pk
    private Integer masterDataId;  // business key
    private String value;  // display value
    private Integer componentTypeId;  // what type of data

    // eg: componentType=RESERVATION_STATUS, value="Confirmed", masterDataId=1
}
