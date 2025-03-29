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
@RequestMapping("/api/v1/test")
@Slf4j
public class TestController {
    
    @GetMapping("/auth")
    public ResponseEntity<?> testAuth(Authentication authentication) {
        log.debug("Authentication: {}", authentication);
        if (authentication != null) {
            return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "isAuthenticated", authentication.isAuthenticated()
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }
}