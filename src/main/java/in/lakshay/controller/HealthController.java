package in.lakshay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// health check controller for monitoring application status
// used by Render.com to verify the app is running
// don't need auth for this endpoint
@RestController
@RequestMapping("/api/v1/health") // health check endpoint
public class HealthController {

    // simple health check that returns app status
    // render.com pings this to make sure we're alive
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP"); // always UP if we can respond
        status.put("timestamp", new Date().toString()); // current time
        status.put("service", "Movie Reservation System API"); // service name
        return ResponseEntity.ok(status);
    }
} // end of HealthController
