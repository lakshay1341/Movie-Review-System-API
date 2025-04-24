package in.lakshay.repo;

import in.lakshay.entity.ComponentType;
import in.lakshay.entity.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {
    List<MasterData> findByComponentType(ComponentType componentType);
    
    Optional<MasterData> findByComponentTypeAndMasterDataId(ComponentType componentType, Integer masterDataId);
    
    Optional<MasterData> findByComponentTypeNameAndMasterDataId(String componentTypeName, Integer masterDataId);
}
