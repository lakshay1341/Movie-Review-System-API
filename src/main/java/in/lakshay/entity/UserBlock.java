package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_blocks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"blocked_user_id", "blocked_by_id"})
})
public class UserBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false)
    private User blockedUser;
    
    @ManyToOne
    @JoinColumn(name = "blocked_by_id", nullable = false)
    private User blockedBy;
    
    @Column(name = "reason")
    private String reason;
    
    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;
    
    @Column(name = "is_admin_block", nullable = false)
    private boolean isAdminBlock;
    
    @PrePersist
    protected void onCreate() {
        blockedAt = LocalDateTime.now();
    }
}
