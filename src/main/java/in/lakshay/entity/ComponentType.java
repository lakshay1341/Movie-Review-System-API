package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "component_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "componentType", cascade = CascadeType.ALL)
    private List<MasterData> masterDataList;
}
