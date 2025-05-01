package in.lakshay.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import in.lakshay.entity.Payment;
import in.lakshay.entity.Reservation;
import in.lakshay.entity.Seat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdfService {

    @Value("${pdf.receipt.directory:receipts}")
    private String receiptDirectory;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Generates a PDF receipt for a payment
     *
     * @param payment The payment for which to generate a receipt
     * @return The path to the generated PDF file
     */
    public String generateReceipt(Payment payment) throws DocumentException, IOException {
        log.info("Generating PDF receipt for payment ID: {}", payment.getId());

        // Create directory if it doesn't exist
        Path dirPath = Paths.get(receiptDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            log.info("Created receipt directory: {}", dirPath.toAbsolutePath());
        }

        // Generate file name
        String fileName = "receipt_" + payment.getId() + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = receiptDirectory + File.separator + fileName;

        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Add content to the document
        addReceiptContent(document, payment);

        document.close();
        log.info("PDF receipt generated successfully at: {}", filePath);

        return filePath;
    }

    private void addReceiptContent(Document document, Payment payment) throws DocumentException {
        Reservation reservation = payment.getReservation();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Movie Ticket Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add receipt details
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // Payment Information
        document.add(new Paragraph("Payment Information", headerFont));
        document.add(new Paragraph("Receipt ID: " + payment.getId(), normalFont));
        document.add(new Paragraph("Payment Date: " +
                (payment.getUpdatedAt() != null ? payment.getUpdatedAt().format(DATETIME_FORMATTER) :
                payment.getCreatedAt().format(DATETIME_FORMATTER)), normalFont));
        document.add(new Paragraph("Payment Status: " + payment.getStatus(), normalFont));
        document.add(new Paragraph("Transaction ID: " + payment.getPaymentIntentId(), normalFont));
        document.add(new Paragraph("Amount Paid: $" + String.format("%.2f", payment.getAmount()), normalFont));
        document.add(Chunk.NEWLINE);

        // Movie and Showtime Information
        document.add(new Paragraph("Movie Information", headerFont));
        document.add(new Paragraph("Movie: " + reservation.getShowtime().getMovie().getTitle(), normalFont));
        document.add(new Paragraph("Theater: " + reservation.getShowtime().getTheater().getName(), normalFont));
        document.add(new Paragraph("Location: " + reservation.getShowtime().getTheater().getLocation(), normalFont));
        document.add(new Paragraph("Date: " + reservation.getShowtime().getShowDate().format(DATE_FORMATTER), normalFont));
        document.add(new Paragraph("Time: " + reservation.getShowtime().getShowTime().format(TIME_FORMATTER), normalFont));
        document.add(Chunk.NEWLINE);

        // Seat Information
        document.add(new Paragraph("Seat Information", headerFont));
        List<Seat> seats = reservation.getSeats();
        String seatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.joining(", "));
        document.add(new Paragraph("Seats: " + seatNumbers, normalFont));
        document.add(new Paragraph("Number of Seats: " + seats.size(), normalFont));
        document.add(Chunk.NEWLINE);

        // Customer Information
        document.add(new Paragraph("Customer Information", headerFont));
        document.add(new Paragraph("Name: " + reservation.getUser().getUserName(), normalFont));
        document.add(new Paragraph("Email: " + reservation.getUser().getEmail(), normalFont));
        document.add(new Paragraph("Reservation ID: " + reservation.getId(), normalFont));
        document.add(new Paragraph("Reservation Date: " + reservation.getReservationTime().format(DATETIME_FORMATTER), normalFont));
        document.add(Chunk.NEWLINE);

        // Price Breakdown
        document.add(new Paragraph("Price Breakdown", headerFont));
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Add table headers
        PdfPCell cell1 = new PdfPCell(new Phrase("Item", headerFont));
        PdfPCell cell2 = new PdfPCell(new Phrase("Quantity", headerFont));
        PdfPCell cell3 = new PdfPCell(new Phrase("Price", headerFont));

        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);

        // Add ticket price row
        table.addCell("Movie Ticket");
        table.addCell(String.valueOf(seats.size()));
        table.addCell("$" + String.format("%.2f", reservation.getShowtime().getPrice()));

        // Add total row
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total", headerFont));
        totalLabelCell.setColspan(2);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase("$" + String.format("%.2f", payment.getAmount()), headerFont));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(totalValueCell);

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Add footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Thank you for your purchase! Please present this receipt at the theater.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
