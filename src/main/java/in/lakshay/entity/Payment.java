package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// payment entity - tracks payment info for reservations
@Entity
@Data
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // convenient for testing
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // one payment per reservation
    @JoinColumn(name = "reservation_id")
    private Reservation reservation; // which reservation this payment is for

    @Column(nullable = false)
    private String paymentIntentId; // stripe payment intent id

    @Column(nullable = false)
    private Double amount; // how much was paid

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // store enum as string in db
    private PaymentStatus status; // current payment status

    @Column(nullable = false)
    private LocalDateTime createdAt; // when payment was created

    @Column
    private LocalDateTime updatedAt; // when payment was last updated

    @Column
    private String receiptUrl; // url to online receipt (from stripe)

    @Column
    private String pdfReceiptPath; // path to pdf receipt on server

    // payment statuses - matches stripe statuses
    public enum PaymentStatus {
        PENDING,  // initial state
        SUCCEEDED, // payment completed successfully
        FAILED,   // payment failed
        REFUNDED  // payment was refunded
    }

    // todo: add method to generate pdf receipt
}
