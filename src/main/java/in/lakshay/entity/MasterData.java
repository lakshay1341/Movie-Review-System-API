package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "master_data_id", nullable = false)
    private Integer masterDataId;

    @Column(name = "data_value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "component_type_id", nullable = false)
    private ComponentType componentType;
}
