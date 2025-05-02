package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// tracks user votes on reviews (upvote/downvote)
@Entity
@Data
@Table(name = "review_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"review_id", "user_id"}) // user can only vote once per review
})
public class ReviewVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;  // which review was voted on

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // who voted

    @Column(name = "is_upvote", nullable = false)
    private boolean isUpvote;  // true=upvote, false=downvote

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;  // when the vote was cast

    // auto-set timestamp on create
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now(); // set current time
    }

    // todo: maybe add method to toggle vote type?
}
