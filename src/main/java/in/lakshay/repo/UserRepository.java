package in.lakshay.repo;

import in.lakshay.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

// user data access - auth, profile, admin stuff
public interface UserRepository extends JpaRepository<User, Long> {
	// find by username - used for login
	Optional<User> findByUserName(String userName);

	// find by email - used for registration validation
	Optional<User> findByEmail(String email);

	// search users by name or email - for admin panel
	// case insensitive search on both fields
	Page<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String userName, String email, Pageable pageable);

	// This query will be updated when we add the active field to the User entity
	// @Query("SELECT u FROM User u WHERE u.active = true")
	// Page<User> findAllActive(Pageable pageable);
	// TODO: implement user activation status tracking

	// For now, we'll just return all users
	Page<User> findAll(Pageable pageable); // admin user list

	// count reservations for a user - for profile stats
	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = ?1")
	int countReservationsByUserId(Long userId);

	// count reviews written by a user - for profile stats
	@Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = ?1")
	int countReviewsByUserId(Long userId);

	// These methods will be implemented in the future when we add the fields to the database
	// Long countByActiveTrue(); // count active users
	// Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime); // new user signups
}
