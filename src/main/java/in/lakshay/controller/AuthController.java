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
import org.springframework.security.core.Authentication; // duplicate import, oops - fix later

@RestController
@RequestMapping("/api/v1/auth") // login/register endpoints
@Slf4j // for logging
public class AuthController {
    // too many autowired fields... should refactor to constructor injection someday
    @Autowired
    private AuthenticationManager authenticationManager; // handles auth

    @Autowired
    private UserRepository userRepository; // user data access

    @Autowired
    private RoleRepository roleRepository; // role lookups

    @Autowired
    private PasswordEncoder passwordEncoder; // for hashing passwords

    @Autowired
    private JwtUtil jwtUtil; // generates tokens

    @Autowired
    private MessageSource messageSource; // i18n

    @Autowired
    private UserService userService; // user business logic

    @RateLimiter(name = "basic") // prevent brute force attacks
    @PostMapping("/login") // POST /api/v1/auth/login
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Retrieve user to check if it exists
            // this will throw if user not found
            User user = userRepository.findByUserName(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        log.warn("Login failed: User not found: {}", loginRequest.getUsername());
                        return new UsernameNotFoundException("User not found"); // spring security exception
                    });

            // debug password format issues - had problems with bcrypt prefix
            // TODO: remove this after we fix all password issues
            log.debug("User found: {}, Role: {}, Password format: {}",
                    user.getUserName(),
                    user.getRole().getName(),
                    user.getPassword().startsWith("{bcrypt}") ? "Has {bcrypt} prefix" : "Missing {bcrypt} prefix");

            // Attempt authentication - this will check password
            // throws AuthenticationException if password wrong
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            log.debug("Authentication successful: {}", authentication.isAuthenticated());
            SecurityContextHolder.getContext().setAuthentication(authentication); // store in context

            // Update last login time - for user stats
            userService.updateLastLoginTime(loginRequest.getUsername());

            // generate JWT token for the user - valid for 1hr
            String token = jwtUtil.generateToken(user);

            log.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "auth.login.success", // i18n key
                    new LoginResponse(token) // return token to client
            ));
        } catch (AuthenticationException e) {
            // wrong password or other auth issues
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(new ApiResponse<>(false, "auth.login.failure", null));
        }
    }

    @GetMapping("/status") // check if user is logged in
    public ResponseEntity<?> getAuthStatus(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // user is logged in
            log.info("Auth status check: User {} is authenticated", authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "auth.status.authenticated",
                    Map.of(
                        "username", authentication.getName(),
                        "authorities", authentication.getAuthorities(), // roles
                        "isAuthenticated", true
                    )
            ));
        } else {
            // user is not logged in
            log.info("Auth status check: No authenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                    .body(new ApiResponse<>(
                            false,
                            "auth.status.unauthenticated",
                            Map.of("isAuthenticated", false)
                    ));
        }
    }

    @PostMapping("/register") // new user registration
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for username: {}", registerRequest.getUsername());

        // check if username already exists - can't have duplicates
        if (userRepository.findByUserName(registerRequest.getUsername()).isPresent()) {
            log.warn("Registration failed: Username already exists: {}", registerRequest.getUsername());
            String message = messageSource.getMessage("user.exists", null, Locale.getDefault());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    message, // "Username already exists"
                    null
            ));
        }

        // get the default user role - all new users are regular users
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            // this should never happen unless db is messed up
            // probably means schema.sql didn't run correctly
            log.error("Registration failed: ROLE_USER not found in database!");
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                    false,
                    "system.configuration.error", // "System configuration error"
                    null
            ));
        }

        // create and save the new user
        User user = new User();
        user.setUserName(registerRequest.getUsername()); // username for login
        user.setEmail(registerRequest.getEmail()); // for notifications
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // hash the password
        user.setRole(userRole); // assign regular user role
        userRepository.save(user); // save to db

        log.info("User registered successfully: {}", registerRequest.getUsername());

        // return success response
        String message = messageSource.getMessage("user.registered", null, Locale.getDefault());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                message, // "User registered successfully"
                new RegisterResponse(registerRequest.getUsername())
        ));
    }
} // end of AuthController