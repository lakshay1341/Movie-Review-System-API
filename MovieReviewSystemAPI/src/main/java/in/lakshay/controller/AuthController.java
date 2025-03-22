package in.lakshay.controller;

import in.lakshay.config.JwtUtil;
import in.lakshay.dto.LoginRequest;
import in.lakshay.dto.RegisterRequest;
import in.lakshay.entity.Role;
import in.lakshay.entity.User;
import in.lakshay.repo.RoleRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUserName(loginRequest.getUsername());
            String token = jwtUtil.generateToken(user);
            log.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.warn("Login failed for user {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUserName(registerRequest.getUsername()) != null) {
            log.warn("Registration failed: Username {} already exists", registerRequest.getUsername());
            return ResponseEntity.badRequest().body(messageSource.getMessage("user.exists", null, Locale.getDefault()));
        }
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            log.error("ROLE_USER not found in database");
            return ResponseEntity.status(500).body("ROLE_USER not found");
        }
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(userRole);
        userRepository.save(user);
        log.info("User {} registered successfully", registerRequest.getUsername());
        return ResponseEntity.ok(messageSource.getMessage("user.registered", null, Locale.getDefault()));
    }
}