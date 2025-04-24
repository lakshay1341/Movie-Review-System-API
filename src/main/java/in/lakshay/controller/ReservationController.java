package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.ReservationDTO;
import in.lakshay.dto.ReservationRequest;
import in.lakshay.service.ReservationService;
import in.lakshay.util.Constants;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.RESERVATIONS_PATH)
@Slf4j
@Tag(name = "Reservations", description = "Reservation management APIs")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MessageSource messageSource;

    @RateLimiter(name = "basic")
    @GetMapping("/my-reservations")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's reservations", description = "Returns all reservations for the authenticated user")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyReservations(
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) Integer statusId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching reservations for user: {} with paid filter: {} and status filter: {}", username, paid, statusId);
        List<ReservationDTO> reservations;

        if (paid != null && statusId != null) {
            // Filter by both payment status and reservation status
            reservations = reservationService.getReservationsByUserAndPaymentStatusAndStatusId(username, paid, statusId);
        } else if (paid != null) {
            // Filter by payment status only
            reservations = reservationService.getReservationsByUserAndPaymentStatus(username, paid);
        } else if (statusId != null) {
            // Filter by reservation status only
            reservations = reservationService.getReservationsByUserAndStatusId(username, statusId);
        } else {
            // Get all reservations
            reservations = reservationService.getReservationsByUser(username);
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservations.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservations
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/my-upcoming-reservations")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's upcoming reservations", description = "Returns upcoming reservations for the authenticated user")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyUpcomingReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching upcoming reservations for user: {}", username);
        List<ReservationDTO> reservations = reservationService.getUpcomingReservationsByUser(username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservations.upcoming.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservations
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get reservation by ID", description = "Returns a reservation by its ID")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservationById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        log.info("Fetching reservation with id: {} for user: {}", id, username);
        ReservationDTO reservation = reservationService.getReservationById(id);

        // Check if user is authorized to view this reservation
        if (!isAdmin && !reservation.getUserName().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            false,
                            messageSource.getMessage("reservation.unauthorized", null, LocaleContextHolder.getLocale()),
                            null
                    ));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservation.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservation
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all confirmed reservations", description = "Returns all confirmed reservations (Admin only)")
    public ResponseEntity<ApiResponse<Page<ReservationDTO>>> getAllConfirmedReservations(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching all confirmed reservations");
        Page<ReservationDTO> reservations = reservationService.getAllConfirmedReservations(pageable);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservations.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservations
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/reports/revenue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get revenue report", description = "Returns revenue for a date range (Admin only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Calculating revenue for date range: {} to {}", startDate, endDate);
        Double revenue = reservationService.calculateRevenueForDateRange(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("revenue", revenue != null ? revenue : 0.0);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("revenue.report.success", null, LocaleContextHolder.getLocale()),
                report
        ));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a reservation", description = "Creates a new reservation for the authenticated user (payment required to complete)")
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Creating reservation for user: {} for showtime: {} with seats: {}",
                username, reservationRequest.getShowtimeId(), reservationRequest.getSeatIds());

        try {
            ReservationDTO reservation = reservationService.createReservation(
                    username, reservationRequest.getShowtimeId(), reservationRequest.getSeatIds());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            messageSource.getMessage("reservation.created.success", null, LocaleContextHolder.getLocale()),
                            reservation
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel a reservation", description = "Cancels an existing reservation")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Canceling reservation with id: {} for user: {}", id, username);

        try {
            ReservationDTO canceledReservation = reservationService.cancelReservation(id, username);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("reservation.canceled.success", null, LocaleContextHolder.getLocale()),
                    canceledReservation
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }
}
