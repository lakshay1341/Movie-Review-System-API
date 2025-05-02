package in.lakshay.repo;

import in.lakshay.entity.ComponentType;
import in.lakshay.entity.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// repo for master data (lookup values, status codes, etc)
// used for things like reservation statuses, payment types, etc
@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {
    // find all master data for a component type
    List<MasterData> findByComponentType(ComponentType componentType); // get all values for a type

    // find specific master data by component type and id
    Optional<MasterData> findByComponentTypeAndMasterDataId(ComponentType componentType, Integer masterDataId);

    // same as above but using component type name instead of object
    // more convenient in some cases
    Optional<MasterData> findByComponentTypeNameAndMasterDataId(String componentTypeName, Integer masterDataId); // easier lookup
}
