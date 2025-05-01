package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "upvotes")
    private Integer upvotes = 0;

    @Column(name = "downvotes")
    private Integer downvotes = 0;

    @Column(name = "helpful_tags")
    private String helpfulTags;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.APPROVED;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}