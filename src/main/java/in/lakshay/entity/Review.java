package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// review entity - user reviews for movies
@Entity
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // who wrote the review

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;  // which movie was reviewed

    @Column(nullable = false)
    private String comment;  // the actual review text

    @Column(nullable = false)
    private int rating;  // 1-5 stars

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // when review was created

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // when review was last updated

    @Column(name = "upvotes")
    private Integer upvotes = 0;  // number of upvotes

    @Column(name = "downvotes")
    private Integer downvotes = 0;  // number of downvotes

    @Column(name = "helpful_tags") // comma-separated list of tags
    private String helpfulTags;  // eg: "funny,insightful,accurate"

    @Column(name = "status")
    @Enumerated(EnumType.STRING) // store enum as string in db
    private ReviewStatus status = ReviewStatus.APPROVED;  // default to approved

    // auto-set timestamps on create
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now(); // same as created initially
    }

    // auto-update the updated_at timestamp
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // update the timestamp
    }

    // possible review statuses
    public enum ReviewStatus {
        PENDING,   // waiting for approval
        APPROVED,  // visible to users
        REJECTED   // not shown (spam/inappropriate)
    }

    // todo: add method to check if review is helpful
    // todo: add method to calculate helpfulness score
}