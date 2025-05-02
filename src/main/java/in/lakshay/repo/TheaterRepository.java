package in.lakshay.repo;

import in.lakshay.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// simple repo for theater data
// not much here yet, might add more search methods later
@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    // find theaters by location (city, area, etc)
    List<Theater> findByLocationContainingIgnoreCase(String location); // case insensitive search
}
