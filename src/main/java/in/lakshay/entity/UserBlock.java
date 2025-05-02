package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// tracks when users block other users or admins block users
@Entity
@Data
@Table(name = "user_blocks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blocked_user_id", "blocked_by_id"}) // can't block same user twice
})
public class UserBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false)
    private User blockedUser;  // user who got blocked

    @ManyToOne
    @JoinColumn(name = "blocked_by_id", nullable = false)
    private User blockedBy;  // user who did the blocking

    @Column(name = "reason") // nullable
    private String reason;  // why the block happened

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;  // when the block happened

    @Column(name = "is_admin_block", nullable = false)
    private boolean isAdminBlock;  // true if admin blocked, false if user blocked

    // auto-set timestamp on create
    @PrePersist
    protected void onCreate() {
        blockedAt = LocalDateTime.now(); // set current time
    }

    // todo: maybe add unblock date for temp bans?
}
