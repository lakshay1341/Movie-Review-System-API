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
@RequestMapping("/api/v1/admin/dashboard")
@Slf4j
@Tag(name = "Admin Dashboard", description = "Admin dashboard APIs")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminDashboardController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/metrics")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get dashboard metrics", description = "Returns key metrics for the admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Set default date range to last 30 days if not provided
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(30);
        
        log.info("Fetching dashboard metrics for date range: {} to {}", start, end);
        
        // Calculate metrics
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        Double totalRevenue = reservationService.calculateRevenueForDateRange(start, end);
        


        // Get reservation counts
        Long totalReservations = reservationService.countReservationsForDateRange(startDateTime, endDateTime);
        Long totalConfirmedReservations = reservationService.countConfirmedReservationsForDateRange(startDateTime, endDateTime);
        Long totalCanceledReservations = reservationService.countCanceledReservationsForDateRange(startDateTime, endDateTime);
        // Get total users count
        Long totalUsers = userService.countActiveUsers();
        Long newUsers = userService.countNewUsersForDateRange(startDateTime, endDateTime);
        
        // Create response
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("startDate", start);
        metrics.put("endDate", end);
        metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        metrics.put("totalReservations", totalReservations);
        metrics.put("totalConfirmedReservations", totalConfirmedReservations);
        metrics.put("totalCanceledReservations", totalCanceledReservations);
        metrics.put("reservationCompletionRate", calculatePercentage(totalConfirmedReservations, totalReservations));
        metrics.put("totalUsers", totalUsers);
        metrics.put("newUsers", newUsers);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("dashboard.metrics.success", null, LocaleContextHolder.getLocale()),
                metrics
        ));
    }
    
    private double calculatePercentage(long value, long total) {
        return total > 0 ? (double) value / total * 100 : 0;
    }
}
