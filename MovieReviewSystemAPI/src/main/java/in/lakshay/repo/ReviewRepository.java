package in.lakshay.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.lakshay.entity.Review;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {}
