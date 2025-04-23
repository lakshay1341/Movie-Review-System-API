package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.SeatDTO;
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
@RequestMapping(Constants.SEATS_PATH)
@Slf4j
@Tag(name = "Seats", description = "Seat management APIs")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @Autowired
    private MessageSource messageSource;

    @RateLimiter(name = "basic")
    @GetMapping("/showtimes/{showtimeId}")
    // No authentication required for this endpoint
    @Operation(summary = "Get all seats for a showtime", description = "Returns all seats for a specific showtime (public)")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getSeatsByShowtime(@PathVariable Long showtimeId) {
        log.info("Fetching seats for showtime id: {}", showtimeId);
        List<SeatDTO> seats = seatService.getSeatsByShowtime(showtimeId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("seats.retrieved.success", null, LocaleContextHolder.getLocale()),
                seats
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/showtimes/{showtimeId}/available")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get available seats for a showtime", description = "Returns available seats for a specific showtime")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAvailableSeatsByShowtime(@PathVariable Long showtimeId) {
        log.info("Fetching available seats for showtime id: {}", showtimeId);
        List<SeatDTO> availableSeats = seatService.getAvailableSeatsByShowtime(showtimeId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("seats.available.retrieved.success", null, LocaleContextHolder.getLocale()),
                availableSeats
        ));
    }
}
