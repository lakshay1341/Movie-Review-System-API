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

/**
 * Diagnostic controller for troubleshooting database issues.
 * These endpoints are admin-only and provide information about the database state.
 */
@RestController
@RequestMapping("/api/v1/diagnostics")
@Slf4j
public class DiagnosticController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository;
    
    @Autowired
    private ComponentTypeRepository componentTypeRepository;
    
    @Autowired
    private MasterDataRepository masterDataRepository;
    
    /**
     * Get database diagnostics including entity counts and key data
     * 
     * @return A map containing diagnostic information
     */
    @GetMapping("/database")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseDiagnostics() {
        log.info("Fetching database diagnostics");
        Map<String, Object> diagnostics = new HashMap<>();
        
        // Count entities
        diagnostics.put("userCount", userRepository.count());
        diagnostics.put("reservationCount", reservationRepository.count());
        diagnostics.put("seatCount", seatRepository.count());
        diagnostics.put("showtimeCount", showtimeRepository.count());
        
        // Check if admin user exists
        boolean adminExists = userRepository.findByUserName("admin").isPresent();
        diagnostics.put("adminExists", adminExists);
        
        // Check component types and master data
        Optional<ComponentType> reservationStatusType = componentTypeRepository.findByName("RESERVATION_STATUS");
        diagnostics.put("reservationStatusTypeExists", reservationStatusType.isPresent());
        
        if (reservationStatusType.isPresent()) {
            List<MasterData> statusValues = masterDataRepository.findByComponentType(reservationStatusType.get());
            diagnostics.put("reservationStatusCount", statusValues.size());
            
            // Log the status values for debugging
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
}
