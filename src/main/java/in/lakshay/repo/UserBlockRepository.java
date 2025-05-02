package in.lakshay.repo;

import in.lakshay.entity.User;
import in.lakshay.entity.UserBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// handles user blocking functionality
// users can be blocked by admins or other users
@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    // find specific block between two users
    Optional<UserBlock> findByBlockedUserAndBlockedBy(User blockedUser, User blockedBy);

    // find all blocks for a user (who blocked them)
    List<UserBlock> findByBlockedUser(User blockedUser); // who blocked this user

    // find all users blocked by a specific user
    List<UserBlock> findByBlockedBy(User blockedBy); // who this user blocked

    // find all admin blocks (or user blocks) with pagination
    Page<UserBlock> findByIsAdminBlock(boolean isAdminBlock, Pageable pageable);

    // check if a user is blocked by another user
    // more efficient than findByBlockedUserAndBlockedBy().isPresent()
    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBlock ub WHERE ub.blockedUser = ?1 AND ub.blockedBy = ?2")
    boolean existsByBlockedUserAndBlockedBy(User blockedUser, User blockedBy); // quick check

    // check if a user is blocked by an admin
    // used for auth checks - admin-blocked users can't login
    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBlock ub WHERE ub.blockedUser.id = ?1 AND ub.isAdminBlock = true")
    boolean isUserBlockedByAdmin(Long userId); // for login validation
}
