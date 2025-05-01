package in.lakshay.service;

import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Movie Reservation System}")
    private String appName;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Sends an email with the receipt PDF attached
     * 
     * @param payment The payment for which to send the receipt
     * @param pdfPath The path to the PDF receipt file
     */
    public void sendReceiptEmail(Payment payment, String pdfPath) throws MessagingException {
        log.info("Sending receipt email for payment ID: {}", payment.getId());
        
        Reservation reservation = payment.getReservation();
        String toEmail = reservation.getUser().getEmail();
        String subject = "Your Movie Ticket Receipt - " + reservation.getShowtime().getMovie().getTitle();
        
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        
        // Create HTML content
        String htmlContent = createEmailContent(payment);
        helper.setText(htmlContent, true);
        
        // Attach PDF receipt
        FileSystemResource file = new FileSystemResource(new File(pdfPath));
        helper.addAttachment("Receipt.pdf", file);
        
        emailSender.send(message);
        log.info("Receipt email sent successfully to: {}", toEmail);
    }
    
    private String createEmailContent(Payment payment) {
        Reservation reservation = payment.getReservation();
        
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body>");
        htmlBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        
        // Header
        htmlBuilder.append("<div style='background-color: #3f51b5; color: white; padding: 20px; text-align: center;'>");
        htmlBuilder.append("<h1>").append(appName).append("</h1>");
        htmlBuilder.append("<h2>Your Movie Ticket Receipt</h2>");
        htmlBuilder.append("</div>");
        
        // Content
        htmlBuilder.append("<div style='padding: 20px;'>");
        htmlBuilder.append("<p>Dear ").append(reservation.getUser().getUserName()).append(",</p>");
        htmlBuilder.append("<p>Thank you for your purchase. Your payment has been successfully processed.</p>");
        
        // Movie details
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 15px 0; border-radius: 5px;'>");
        htmlBuilder.append("<h3>Movie Details</h3>");
        htmlBuilder.append("<p><strong>Movie:</strong> ").append(reservation.getShowtime().getMovie().getTitle()).append("</p>");
        htmlBuilder.append("<p><strong>Theater:</strong> ").append(reservation.getShowtime().getTheater().getName()).append("</p>");
        htmlBuilder.append("<p><strong>Location:</strong> ").append(reservation.getShowtime().getTheater().getLocation()).append("</p>");
        htmlBuilder.append("<p><strong>Date:</strong> ").append(reservation.getShowtime().getShowDate().format(DATE_FORMATTER)).append("</p>");
        htmlBuilder.append("<p><strong>Time:</strong> ").append(reservation.getShowtime().getShowTime().format(TIME_FORMATTER)).append("</p>");
        
        // Seat information
        StringBuilder seatNumbers = new StringBuilder();
        reservation.getSeats().forEach(seat -> {
            if (seatNumbers.length() > 0) {
                seatNumbers.append(", ");
            }
            seatNumbers.append(seat.getSeatNumber());
        });
        
        htmlBuilder.append("<p><strong>Seats:</strong> ").append(seatNumbers).append("</p>");
        htmlBuilder.append("<p><strong>Number of Seats:</strong> ").append(reservation.getSeats().size()).append("</p>");
        htmlBuilder.append("</div>");
        
        // Payment details
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 15px 0; border-radius: 5px;'>");
        htmlBuilder.append("<h3>Payment Details</h3>");
        htmlBuilder.append("<p><strong>Amount Paid:</strong> $").append(String.format("%.2f", payment.getAmount())).append("</p>");
        htmlBuilder.append("<p><strong>Transaction ID:</strong> ").append(payment.getPaymentIntentId()).append("</p>");
        htmlBuilder.append("<p><strong>Reservation ID:</strong> ").append(reservation.getId()).append("</p>");
        htmlBuilder.append("</div>");
        
        // Instructions
        htmlBuilder.append("<p>Please find your receipt attached to this email. You can present this receipt or your reservation ID at the theater.</p>");
        htmlBuilder.append("<p>We hope you enjoy the movie!</p>");
        htmlBuilder.append("<p>Best regards,<br>The ").append(appName).append(" Team</p>");
        htmlBuilder.append("</div>");
        
        // Footer
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 12px;'>");
        htmlBuilder.append("<p>This is an automated email. Please do not reply to this message.</p>");
        htmlBuilder.append("<p>&copy; ").append(java.time.Year.now()).append(" ").append(appName).append(". All rights reserved.</p>");
        htmlBuilder.append("</div>");
        
        htmlBuilder.append("</div>");
        htmlBuilder.append("</body></html>");
        
        return htmlBuilder.toString();
    }
}
