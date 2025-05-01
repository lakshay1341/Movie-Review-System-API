package in.lakshay.repo;

import in.lakshay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);

	Optional<User> findByEmail(String email);

	Page<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String userName, String email, Pageable pageable);

	// This query will be updated when we add the active field to the User entity
	// @Query("SELECT u FROM User u WHERE u.active = true")
	// Page<User> findAllActive(Pageable pageable);

	// For now, we'll just return all users
	Page<User> findAll(Pageable pageable);

	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = ?1")
	int countReservationsByUserId(Long userId);

	@Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = ?1")
	int countReviewsByUserId(Long userId);

	// These methods will be implemented in the future when we add the fields to the database
	// Long countByActiveTrue();
	// Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
