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

// generates PDF receipts using iText library

@Service
@Slf4j // logging
public class PdfService {

    @Value("${pdf.receipt.directory:receipts}") // default to 'receipts' folder
    private String receiptDirectory; // where to save the PDFs

    // formatters for dates/times in the PDF
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // just date
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm"); // 24hr time
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // full timestamp

    /**
     * Creates a PDF receipt for a payment
     *
     * @param payment The payment to generate receipt for
     * @return Path to the PDF file
     */
    public String generateReceipt(Payment payment) throws DocumentException, IOException {
        log.info("Generating PDF receipt for payment ID: {}", payment.getId());

        // make sure folder exists
        Path dirPath = Paths.get(receiptDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            log.info("Created receipt directory: {}", dirPath.toAbsolutePath());
        }

        // unique filename with timestamp to avoid collisions
        String fileName = "receipt_" + payment.getId() + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = receiptDirectory + File.separator + fileName;

        // setup the PDF doc - A4 paper size
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open(); // gotta open before writing

        // fill it with content
        addReceiptContent(document, payment);

        document.close(); // important! close when done
        log.info("PDF receipt generated successfully at: {}", filePath);

        return filePath; // return path for email attachment
    }

    // does the actual PDF creation - lots of iText API calls
    private void addReceiptContent(Document document, Payment payment) throws DocumentException {
        Reservation reservation = payment.getReservation();

        // big title at top
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Movie Ticket Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20); // some space after title
        document.add(title);

        // setup fonts we'll use throughout
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // payment section
        document.add(new Paragraph("Payment Information", headerFont));
        document.add(new Paragraph("Receipt ID: " + payment.getId(), normalFont));
        document.add(new Paragraph("Payment Date: " +
                (payment.getUpdatedAt() != null ? payment.getUpdatedAt().format(DATETIME_FORMATTER) :
                payment.getCreatedAt().format(DATETIME_FORMATTER)), normalFont));
        document.add(new Paragraph("Payment Status: " + payment.getStatus(), normalFont));
        document.add(new Paragraph("Transaction ID: " + payment.getPaymentIntentId(), normalFont));
        document.add(new Paragraph("Amount Paid: $" + String.format("%.2f", payment.getAmount()), normalFont));
        document.add(Chunk.NEWLINE); // blank line

        // movie details section
        document.add(new Paragraph("Movie Information", headerFont));
        document.add(new Paragraph("Movie: " + reservation.getShowtime().getMovie().getTitle(), normalFont));
        document.add(new Paragraph("Theater: " + reservation.getShowtime().getTheater().getName(), normalFont));
        document.add(new Paragraph("Location: " + reservation.getShowtime().getTheater().getLocation(), normalFont));
        document.add(new Paragraph("Date: " + reservation.getShowtime().getShowDate().format(DATE_FORMATTER), normalFont));
        document.add(new Paragraph("Time: " + reservation.getShowtime().getShowTime().format(TIME_FORMATTER), normalFont));
        document.add(Chunk.NEWLINE);

        // seat info - use stream to make comma-separated list
        document.add(new Paragraph("Seat Information", headerFont));
        List<Seat> seats = reservation.getSeats();
        String seatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.joining(", ")); // A1, A2, etc
        document.add(new Paragraph("Seats: " + seatNumbers, normalFont));
        document.add(new Paragraph("Number of Seats: " + seats.size(), normalFont));
        document.add(Chunk.NEWLINE);

        // customer details
        document.add(new Paragraph("Customer Information", headerFont));
        document.add(new Paragraph("Name: " + reservation.getUser().getUserName(), normalFont));
        document.add(new Paragraph("Email: " + reservation.getUser().getEmail(), normalFont));
        document.add(new Paragraph("Reservation ID: " + reservation.getId(), normalFont));
        document.add(new Paragraph("Reservation Date: " + reservation.getReservationTime().format(DATETIME_FORMATTER), normalFont));
        document.add(Chunk.NEWLINE);

        // price table - more complex layout
        document.add(new Paragraph("Price Breakdown", headerFont));
        PdfPTable table = new PdfPTable(3); // 3 columns
        table.setWidthPercentage(100); // full width

        // table headers
        PdfPCell cell1 = new PdfPCell(new Phrase("Item", headerFont));
        PdfPCell cell2 = new PdfPCell(new Phrase("Quantity", headerFont));
        PdfPCell cell3 = new PdfPCell(new Phrase("Price", headerFont));

        // center align all headers
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);

        // ticket row
        table.addCell("Movie Ticket");
        table.addCell(String.valueOf(seats.size())); // num tickets
        table.addCell("$" + String.format("%.2f", reservation.getShowtime().getPrice())); // per ticket price

        // total row at bottom
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total", headerFont));
        totalLabelCell.setColspan(2); // spans 2 cols
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase("$" + String.format("%.2f", payment.getAmount()), headerFont));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(totalValueCell);

        document.add(table);
        document.add(Chunk.NEWLINE);

        // thank you msg at bottom
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Thank you for your purchase! Please present this receipt at the theater.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
