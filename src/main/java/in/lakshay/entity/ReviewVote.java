package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "review_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"review_id", "user_id"})
})
public class ReviewVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "is_upvote", nullable = false)
    private boolean isUpvote;
    
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
    
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }
}
