package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.entity.ComponentType;
import in.lakshay.entity.MasterData;
import in.lakshay.repo.ComponentTypeRepository;
import in.lakshay.repo.MasterDataRepository;
import in.lakshay.repo.ReservationRepository;
import in.lakshay.repo.SeatRepository;
import in.lakshay.repo.ShowtimeRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// for troubleshooting database issues
// admin-only endpoints to check db state when things go wrong
@RestController
@RequestMapping("/api/v1/diagnostics") // base path for diagnostic endpoints
@Slf4j // logging
public class DiagnosticController {

    @Autowired // TODO: switch to constructor injection
    private UserRepository userRepository; // for user counts

    @Autowired
    private ReservationRepository reservationRepository; // for reservation counts

    @Autowired
    private SeatRepository seatRepository; // for seat counts

    @Autowired
    private ShowtimeRepository showtimeRepository; // for showtime counts

    @Autowired
    private ComponentTypeRepository componentTypeRepository; // for component types

    @Autowired
    private MasterDataRepository masterDataRepository; // for master data

    // get database diagnostics including entity counts and key data
    // useful for checking if db is properly initialized
    @GetMapping("/database")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseDiagnostics() {
        log.info("Fetching database diagnostics");
        Map<String, Object> diagnostics = new HashMap<>();

        // Count entities - basic health check
        diagnostics.put("userCount", userRepository.count());
        diagnostics.put("reservationCount", reservationRepository.count());
        diagnostics.put("seatCount", seatRepository.count()); // should be a lot
        diagnostics.put("showtimeCount", showtimeRepository.count());

        // Check if admin user exists - critical check
        boolean adminExists = userRepository.findByUserName("admin").isPresent();
        diagnostics.put("adminExists", adminExists);

        // Check component types and master data
        // this is important for reservation status codes
        Optional<ComponentType> reservationStatusType = componentTypeRepository.findByName("RESERVATION_STATUS");
        diagnostics.put("reservationStatusTypeExists", reservationStatusType.isPresent());

        if (reservationStatusType.isPresent()) {
            List<MasterData> statusValues = masterDataRepository.findByComponentType(reservationStatusType.get());
            diagnostics.put("reservationStatusCount", statusValues.size());

            // Log the status values for debugging
            // helps identify missing statuses
            Map<Integer, String> statusMap = new HashMap<>();
            statusValues.forEach(status -> {
                statusMap.put(status.getMasterDataId(), status.getValue());
                log.info("Status ID: {}, Value: {}", status.getMasterDataId(), status.getValue());
            });
            diagnostics.put("reservationStatuses", statusMap);
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Database diagnostics retrieved successfully",
                diagnostics
        ));
    }
} // end of DiagnosticController
