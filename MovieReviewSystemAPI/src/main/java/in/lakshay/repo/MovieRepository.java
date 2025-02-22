package in.lakshay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.lakshay.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {}
