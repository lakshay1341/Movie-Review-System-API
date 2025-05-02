package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.SeatDTO;
import in.lakshay.entity.Seat;
import in.lakshay.entity.Showtime;
import in.lakshay.repo.ShowtimeRepository;
import in.lakshay.service.SeatService;
import in.lakshay.util.Constants;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.SEATS_PATH) // /api/v1/seats
@Slf4j // for logging
@Tag(name = "Seats", description = "Seat management APIs") // swagger docs
public class SeatController {
    @Autowired // TODO: switch to constructor injection
    private SeatService seatService; // handles seat business logic

    @Autowired
    private ShowtimeRepository showtimeRepository; // for direct showtime access

    @Autowired
    private MessageSource messageSource; // i18n

    @RateLimiter(name = "basic") // prevent abuse
    @GetMapping("/showtimes/{showtimeId}") // get all seats for a showtime
    // No authentication required for this endpoint - public access
    @Operation(summary = "Get all seats for a showtime", description = "Returns all seats for a specific showtime (public)")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getSeatsByShowtime(@PathVariable Long showtimeId) {
        log.info("Fetching seats for showtime id: {}", showtimeId);
        // get all seats regardless of reservation status
        List<SeatDTO> seats = seatService.getSeatsByShowtime(showtimeId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("seats.retrieved.success", null, LocaleContextHolder.getLocale()),
                seats
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/showtimes/{showtimeId}/available") // get only available seats
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Get available seats for a showtime", description = "Returns available seats for a specific showtime")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAvailableSeatsByShowtime(@PathVariable Long showtimeId) {
        log.info("Fetching available seats for showtime id: {}", showtimeId);
        // only get seats that aren't reserved yet
        List<SeatDTO> availableSeats = seatService.getAvailableSeatsByShowtime(showtimeId);

        // used for seat selection during reservation process
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("seats.available.retrieved.success", null, LocaleContextHolder.getLocale()),
                availableSeats
        ));
    }

    @RateLimiter(name = "basic")
    @PostMapping("/showtimes/{showtimeId}/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @Operation(summary = "Create seats for a showtime", description = "Manually creates seats for a specific showtime (Admin only)")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> createSeatsForShowtime(@PathVariable Long showtimeId) {
        log.info("Manually creating seats for showtime id: {}", showtimeId);

        // Force creation of seats for this showtime
        List<SeatDTO> seats = seatService.getSeatsByShowtime(showtimeId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("seats.created.success", null, LocaleContextHolder.getLocale()),
                seats
        ));
    }
} // end of SeatController
