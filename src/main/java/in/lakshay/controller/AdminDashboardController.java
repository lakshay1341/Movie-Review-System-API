package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.service.ReservationService;
import in.lakshay.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard") // base path for admin dashboard endpoints
@Slf4j // logging
@Tag(name = "Admin Dashboard", description = "Admin dashboard APIs") // swagger docs
@PreAuthorize("hasRole('ROLE_ADMIN')") // admin only for all endpoints
public class AdminDashboardController {

    @Autowired // TODO: switch to constructor injection
    private ReservationService reservationService; // for reservation metrics

    @Autowired
    private UserService userService; // for user metrics

    @Autowired
    private MessageSource messageSource; // i18n

    @GetMapping("/metrics") // get dashboard metrics
    @RateLimiter(name = "basic") // prevent abuse
    @Operation(summary = "Get dashboard metrics", description = "Returns key metrics for the admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Set default date range to last 30 days if not provided
        // makes it easier for admins to get quick stats
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(30);

        log.info("Fetching dashboard metrics for date range: {} to {}", start, end);

        // Calculate metrics - need to convert dates to datetime for some queries
        LocalDateTime startDateTime = start.atStartOfDay(); // beginning of start day
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX); // end of end day

        // get total revenue for the period
        Double totalRevenue = reservationService.calculateRevenueForDateRange(start, end);

        // Get reservation counts - these are the main KPIs
        Long totalReservations = reservationService.countReservationsForDateRange(startDateTime, endDateTime);
        Long totalConfirmedReservations = reservationService.countConfirmedReservationsForDateRange(startDateTime, endDateTime);
        Long totalCanceledReservations = reservationService.countCanceledReservationsForDateRange(startDateTime, endDateTime);

        // Get user metrics
        Long totalUsers = userService.countActiveUsers(); // all active users
        Long newUsers = userService.countNewUsersForDateRange(startDateTime, endDateTime); // new signups

        // Create response with all metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("startDate", start);
        metrics.put("endDate", end);
        metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0); // default to 0 if null
        metrics.put("totalReservations", totalReservations);
        metrics.put("totalConfirmedReservations", totalConfirmedReservations);
        metrics.put("totalCanceledReservations", totalCanceledReservations);
        metrics.put("reservationCompletionRate", calculatePercentage(totalConfirmedReservations, totalReservations)); // %
        metrics.put("totalUsers", totalUsers);
        metrics.put("newUsers", newUsers);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("dashboard.metrics.success", null, LocaleContextHolder.getLocale()),
                metrics
        ));
    }

    // helper method to calculate percentages
    // avoids division by zero errors
    private double calculatePercentage(long value, long total) {
        return total > 0 ? (double) value / total * 100 : 0;
    }
} // end of AdminDashboardController
