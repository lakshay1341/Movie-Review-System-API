package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// defines categories for master data (eg: RESERVATION_STATUS, PAYMENT_STATUS)
@Entity
@Table(name = "component_types")
@Data
@NoArgsConstructor // jpa needs this
@AllArgsConstructor // nice for testing
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // pk

    @Column(nullable = false, unique = true)
    private String name;  // eg: "RESERVATION_STATUS", "PAYMENT_STATUS"

    // all master data values for this type
    @OneToMany(mappedBy = "componentType", cascade = CascadeType.ALL)
    private List<MasterData> masterDataList;  // eg: all reservation statuses

    // todo: add description field?
    // todo: add display name field for UI?
}
