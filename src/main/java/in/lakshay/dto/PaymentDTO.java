package in.lakshay.dto;

import in.lakshay.entity.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// payment info for stripe integration
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long reservationId; // which reservation this is for
    private String paymentIntentId; // stripe payment intent
    private Double amount;
    private PaymentStatus status; // PENDING, COMPLETED, FAILED etc

    // timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // receipt stuff
    private String receiptUrl; // online receipt url
    private String pdfReceiptPath; // local path to pdf receipt
}
