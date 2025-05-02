package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// lookup table for various system values (statuses, types, etc)
@Entity
@Table(name = "master_data")
@Data
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // convenient for testing
public class MasterData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // pk

    @Column(name = "master_data_id", nullable = false)
    private Integer masterDataId;  // id within the component type (eg: 1=CONFIRMED for reservation status)

    @Column(name = "data_value", nullable = false)
    private String value;  // display value (eg: "Confirmed")

    @ManyToOne
    @JoinColumn(name = "component_type_id", nullable = false)
    private ComponentType componentType;  // which type this belongs to (eg: RESERVATION_STATUS)

    // todo: add active flag to disable values without deleting?
}
