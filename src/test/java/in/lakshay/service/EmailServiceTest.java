package in.lakshay.service;

import in.lakshay.entity.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private Payment payment;
    private String testPdfPath;

    @BeforeEach
    void setUp() {
        // Set test values
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "appName", "Test Movie System");
        
        testPdfPath = "test-receipts/test-receipt.pdf";
        
        // Set up test data
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setGenre("Action");
        movie.setReleaseYear(2023);
        
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Test Theater");
        theater.setLocation("Test Location");
        theater.setCapacity(100);
        
        Showtime showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setShowDate(LocalDate.now());
        showtime.setShowTime(LocalTime.of(18, 0));
        showtime.setTotalSeats(100);
        showtime.setAvailableSeats(50);
        showtime.setPrice(10.0);
        
        List<Seat> seats = new ArrayList<>();
        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setSeatNumber("A1");
        seat1.setShowtime(showtime);
        seat1.setIsReserved(true);
        
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setSeatNumber("A2");
        seat2.setShowtime(showtime);
        seat2.setIsReserved(true);
        
        seats.add(seat1);
        seats.add(seat2);
        
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setShowtime(showtime);
        reservation.setSeats(seats);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatusId(2); // PAID
        reservation.setPaid(true);
        reservation.setTotalPrice(20.0);
        
        payment = new Payment();
        payment.setId(1L);
        payment.setReservation(reservation);
        payment.setPaymentIntentId("pi_test_123456");
        payment.setAmount(20.0);
        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSendReceiptEmail() throws MessagingException {
        // Mock JavaMailSender
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // Call the method
        emailService.sendReceiptEmail(payment, testPdfPath);
        
        // Verify that send was called
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}
