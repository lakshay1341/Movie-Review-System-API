package in.lakshay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status.
 * This endpoint is used by Render.com to verify the application is running.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    
    /**
     * Simple health check endpoint that returns the application status.
     * 
     * @return A map containing the status and timestamp
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", new Date().toString());
        status.put("service", "Movie Reservation System API");
        return ResponseEntity.ok(status);
    }
}
