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
import java.util.stream.Collectors; // needed for joining strings in logs

@RestController
@RequestMapping(Constants.RESERVATIONS_PATH) // /api/v1/reservations
@Slf4j // for logging stuff
@Tag(name = "Reservations", description = "Reservation management APIs") // swagger docs
public class ReservationController {
    @Autowired // todo: switch to constructor injection someday when I have time
    private ReservationService reservationService; // handles business logic

    @Autowired
    private MessageSource messageSource; // for i18n messages

    @RateLimiter(name = "basic") // prevent abuse from bots
    @GetMapping("/my-reservations")
    @PreAuthorize("isAuthenticated()") // user must be logged in
    @Operation(summary = "Get user's reservations", description = "Returns all reservations for the authenticated user")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyReservations(
            @RequestParam(required = false) Boolean paid, // filter by payment status
            @RequestParam(required = false) Integer statusId) { // filter by reservation status
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching reservations for user: {} with paid filter: {} and status filter: {}", username, paid, statusId);
        List<ReservationDTO> reservations;

        // lots of if/else but it's the clearest way to handle the filters
        // could refactor this later but works fine for now
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
            // Get all reservations - no filters applied
            reservations = reservationService.getReservationsByUser(username);
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservations.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservations
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/my-upcoming-reservations") // only future shows
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's upcoming reservations", description = "Returns upcoming reservations for the authenticated user")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getMyUpcomingReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("API call: Fetching upcoming reservations for user: {}", username);

        // get reservations for future showtimes only
        List<ReservationDTO> reservations = reservationService.getUpcomingReservationsByUser(username);
        log.info("Returning {} upcoming reservations for user: {}", reservations.size(), username);

        // Log the IDs of the reservations being returned - helps with debugging
        // this is super useful when users report issues
        if (!reservations.isEmpty()) {
            String reservationIds = reservations.stream()
                    .map(r -> String.valueOf(r.getId())) // convert Long to String
                    .collect(Collectors.joining(", ")); // comma-separated list
            log.info("Upcoming reservation IDs for user {}: {}", username, reservationIds);
        } // empty list is fine, just means no upcoming shows

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
        // check if user is admin - they can see any reservation
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        log.info("Fetching reservation with id: {} for user: {}", id, username);
        ReservationDTO reservation = reservationService.getReservationById(id); // might throw 404

        // Check if user is authorized to view this reservation
        // users can only see their own reservations unless they're admin
        if (!isAdmin && !reservation.getUsername().equals(username)) {
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
    @Operation(summary = "Get all reservations", description = "Returns all reservations regardless of status (Admin only)")
    public ResponseEntity<ApiResponse<Page<ReservationDTO>>> getAllConfirmedReservations(
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Fetching all reservations");
        Page<ReservationDTO> reservations = reservationService.getAllConfirmedReservations(pageable);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("reservations.retrieved.success", null, LocaleContextHolder.getLocale()),
                reservations
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/reports/revenue") // admin report endpoint
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admins only!!!
    @Operation(summary = "Get revenue report", description = "Returns revenue for a date range (Admin only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Calculating revenue for date range: {} to {}", startDate, endDate);
        Double revenue = reservationService.calculateRevenueForDateRange(startDate, endDate);

        // build response with all the data frontend needs
        // could add more metrics here later
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("revenue", revenue != null ? revenue : 0.0); // default to 0 if null

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("revenue.report.success", null, LocaleContextHolder.getLocale()),
                report
        )); // success!
    }

    @PostMapping // create new reservation
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Create a reservation", description = "Creates a new reservation for the authenticated user (payment required to complete)")
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Creating reservation for user: {} for showtime: {} with seats: {}",
                username, reservationRequest.getShowtimeId(), reservationRequest.getSeatIds());

        try {
            // this might fail if seats are already taken - race condition
            // TODO: maybe add some kind of temporary seat locking mechanism?
            ReservationDTO reservation = reservationService.createReservation(
                    username, reservationRequest.getShowtimeId(), reservationRequest.getSeatIds());

            // 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            messageSource.getMessage("reservation.created.success", null, LocaleContextHolder.getLocale()),
                            reservation
                    ));
        } catch (RuntimeException e) {
            // something went wrong - probably seats already taken
            // or showtime is in the past or something
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(), // pass error msg to client
                            null
                    ));
        }
    }

    @DeleteMapping("/{id}") // cancel reservation
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel a reservation", description = "Cancels an existing reservation")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Canceling reservation with id: {} for user: {}", id, username);

        try {
            // this will check if user owns the reservation or is admin
            ReservationDTO canceledReservation = reservationService.cancelReservation(id, username);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("reservation.canceled.success", null, LocaleContextHolder.getLocale()),
                    canceledReservation
            ));
        } catch (RuntimeException e) {
            // something went wrong - maybe reservation already canceled
            // or showtime already started
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }
} // end of ReservationController
