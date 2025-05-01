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

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Optional<UserBlock> findByBlockedUserAndBlockedBy(User blockedUser, User blockedBy);
    
    List<UserBlock> findByBlockedUser(User blockedUser);
    
    List<UserBlock> findByBlockedBy(User blockedBy);
    
    Page<UserBlock> findByIsAdminBlock(boolean isAdminBlock, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBlock ub WHERE ub.blockedUser = ?1 AND ub.blockedBy = ?2")
    boolean existsByBlockedUserAndBlockedBy(User blockedUser, User blockedBy);
    
    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBlock ub WHERE ub.blockedUser.id = ?1 AND ub.isAdminBlock = true")
    boolean isUserBlockedByAdmin(Long userId);
}
