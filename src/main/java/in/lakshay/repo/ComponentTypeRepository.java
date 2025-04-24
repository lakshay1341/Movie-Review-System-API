package in.lakshay.repo;

import in.lakshay.entity.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentType, Integer> {
    Optional<ComponentType> findByName(String name);
}
