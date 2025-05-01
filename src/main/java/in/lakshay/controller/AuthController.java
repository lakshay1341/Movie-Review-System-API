package in.lakshay.controller;

import in.lakshay.config.JwtUtil;
import in.lakshay.dto.*;
import in.lakshay.entity.Role;
import in.lakshay.entity.User;
import in.lakshay.repo.RoleRepository;
import in.lakshay.repo.UserRepository;
import in.lakshay.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Map;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    @RateLimiter(name = "basic")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Retrieve user to check if it exists
            User user = userRepository.findByUserName(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        log.warn("Login failed: User not found: {}", loginRequest.getUsername());
                        return new UsernameNotFoundException("User not found");
                    });

            log.debug("User found: {}, Role: {}, Password format: {}",
                    user.getUserName(),
                    user.getRole().getName(),
                    user.getPassword().startsWith("{bcrypt}") ? "Has {bcrypt} prefix" : "Missing {bcrypt} prefix");

            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            log.debug("Authentication successful: {}", authentication.isAuthenticated());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Update last login time
            userService.updateLastLoginTime(loginRequest.getUsername());

            String token = jwtUtil.generateToken(user);

            log.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "auth.login.success",
                    new LoginResponse(token)
            ));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "auth.login.failure", null));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("Auth status check: User {} is authenticated", authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "auth.status.authenticated",
                    Map.of(
                        "username", authentication.getName(),
                        "authorities", authentication.getAuthorities(),
                        "isAuthenticated", true
                    )
            ));
        } else {
            log.info("Auth status check: No authenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            false,
                            "auth.status.unauthenticated",
                            Map.of("isAuthenticated", false)
                    ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUserName(registerRequest.getUsername()).isPresent()) {
            String message = messageSource.getMessage("user.exists", null, Locale.getDefault());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    message,
                    null
            ));
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                    false,
                    "system.configuration.error",
                    null
            ));
        }

        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(userRole);
        userRepository.save(user);

        String message = messageSource.getMessage("user.registered", null, Locale.getDefault());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                message,
                new RegisterResponse(registerRequest.getUsername())
        ));
    }
}