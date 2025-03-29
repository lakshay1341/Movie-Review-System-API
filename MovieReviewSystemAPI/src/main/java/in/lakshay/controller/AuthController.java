package in.lakshay.controller;

import in.lakshay.config.JwtUtil;
import in.lakshay.dto.*;
import in.lakshay.entity.Role;
import in.lakshay.entity.User;
import in.lakshay.repo.RoleRepository;
import in.lakshay.repo.UserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

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

    @RateLimiter(name = "basic")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUserName(loginRequest.getUsername());
            String token = jwtUtil.generateToken(user);

            log.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "auth.login.success",
                    new LoginResponse(token)
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "auth.login.failure", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUserName(registerRequest.getUsername()) != null) {
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