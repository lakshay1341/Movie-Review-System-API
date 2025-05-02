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
import java.time.format.DateTimeFormatter; // for formatting dates in emails

@Service
@Slf4j // for logging
public class EmailService {
    // email stuff - sends receipts etc

    @Autowired // spring's email client
    private JavaMailSender emailSender; // does the actual sending

    @Value("${spring.mail.username}") // from application.properties
    private String fromEmail; // sender address

    @Value("${app.name:Movie Reservation System}") // app name with default
    private String appName; // used in email template

    // date formatters for email content
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2023-05-15
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); // 14:30

    // sends a receipt email after payment
    // includes PDF attachment and fancy HTML content
    public void sendReceiptEmail(Payment payment, String pdfPath) throws MessagingException {
        log.info("Sending receipt email for payment ID: {}", payment.getId());

        Reservation reservation = payment.getReservation();
        String toEmail = reservation.getUser().getEmail(); // where to send it
        String subject = "Your Movie Ticket Receipt - " + reservation.getShowtime().getMovie().getTitle();

        // create a multipart email (html + attachment)
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

        helper.setFrom(fromEmail); // from us
        helper.setTo(toEmail); // to customer
        helper.setSubject(subject);

        // Create HTML content with movie/payment details
        String htmlContent = createEmailContent(payment);
        helper.setText(htmlContent, true); // true = html content

        // Attach the PDF receipt
        FileSystemResource file = new FileSystemResource(new File(pdfPath));
        helper.addAttachment("Receipt.pdf", file); // attachment name

        // send the email - might throw MessagingException
        emailSender.send(message);
        log.info("Receipt email sent successfully to: {}", toEmail);
    }

    // builds the HTML email content
    // this is super verbose but it works so ¯\_(ツ)_/¯
    private String createEmailContent(Payment payment) {
        Reservation reservation = payment.getReservation();

        // use StringBuilder cuz string concat in loops = bad
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body>");
        htmlBuilder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");

        // Header - blue bar with app name
        htmlBuilder.append("<div style='background-color: #3f51b5; color: white; padding: 20px; text-align: center;'>");
        htmlBuilder.append("<h1>").append(appName).append("</h1>");
        htmlBuilder.append("<h2>Your Movie Ticket Receipt</h2>");
        htmlBuilder.append("</div>");

        // Content section
        htmlBuilder.append("<div style='padding: 20px;'>");
        htmlBuilder.append("<p>Dear ").append(reservation.getUser().getUserName()).append(",</p>");
        htmlBuilder.append("<p>Thank you for your purchase. Your payment has been successfully processed.</p>");

        // Movie details box - gray background
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 15px 0; border-radius: 5px;'>");
        htmlBuilder.append("<h3>Movie Details</h3>");
        htmlBuilder.append("<p><strong>Movie:</strong> ").append(reservation.getShowtime().getMovie().getTitle()).append("</p>");
        htmlBuilder.append("<p><strong>Theater:</strong> ").append(reservation.getShowtime().getTheater().getName()).append("</p>");
        htmlBuilder.append("<p><strong>Location:</strong> ").append(reservation.getShowtime().getTheater().getLocation()).append("</p>");
        htmlBuilder.append("<p><strong>Date:</strong> ").append(reservation.getShowtime().getShowDate().format(DATE_FORMATTER)).append("</p>");
        htmlBuilder.append("<p><strong>Time:</strong> ").append(reservation.getShowtime().getShowTime().format(TIME_FORMATTER)).append("</p>");

        // build comma-separated list of seat numbers
        StringBuilder seatNumbers = new StringBuilder();
        reservation.getSeats().forEach(seat -> {
            if (seatNumbers.length() > 0) {
                seatNumbers.append(", "); // comma between seats
            }
            seatNumbers.append(seat.getSeatNumber());
        });

        htmlBuilder.append("<p><strong>Seats:</strong> ").append(seatNumbers).append("</p>");
        htmlBuilder.append("<p><strong>Number of Seats:</strong> ").append(reservation.getSeats().size()).append("</p>");
        htmlBuilder.append("</div>");

        // Payment details box - also gray
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 15px 0; border-radius: 5px;'>");
        htmlBuilder.append("<h3>Payment Details</h3>");
        htmlBuilder.append("<p><strong>Amount Paid:</strong> $").append(String.format("%.2f", payment.getAmount())).append("</p>"); // 2 decimal places
        htmlBuilder.append("<p><strong>Transaction ID:</strong> ").append(payment.getPaymentIntentId()).append("</p>"); // stripe ID
        htmlBuilder.append("<p><strong>Reservation ID:</strong> ").append(reservation.getId()).append("</p>"); // our internal ID
        htmlBuilder.append("</div>");

        // Instructions and sign-off
        htmlBuilder.append("<p>Please find your receipt attached to this email. You can present this receipt or your reservation ID at the theater.</p>");
        htmlBuilder.append("<p>We hope you enjoy the movie!</p>");
        htmlBuilder.append("<p>Best regards,<br>The ").append(appName).append(" Team</p>");
        htmlBuilder.append("</div>");

        // Footer with copyright
        htmlBuilder.append("<div style='background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 12px;'>");
        htmlBuilder.append("<p>This is an automated email. Please do not reply to this message.</p>");
        htmlBuilder.append("<p>&copy; ").append(java.time.Year.now()).append(" ").append(appName).append(". All rights reserved.</p>");
        htmlBuilder.append("</div>");

        htmlBuilder.append("</div>");
        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString(); // final HTML string
    }
}
