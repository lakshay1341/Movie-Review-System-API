package in.lakshay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test") // test endpoints - for debugging only
@Slf4j // logging
public class TestController {

    @GetMapping("/auth") // test authentication
    public ResponseEntity<?> testAuth(Authentication authentication) {
        log.debug("Authentication: {}", authentication);
        if (authentication != null) {
            // user is authenticated - return details
            return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(), // roles
                "isAuthenticated", authentication.isAuthenticated()
            ));
        }
        // user is not authenticated
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }
} // end of TestController