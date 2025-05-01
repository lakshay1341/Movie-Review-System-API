package in.lakshay.exception;

import in.lakshay.dto.ApiResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitExceededException(RequestNotPermitted ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(new ApiResponse<>(false, "system.rate.limit.exceeded", null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getErrors());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, "validation.failed", ex.getErrors()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>(false, "auth.login.failure", null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>(false, "auth.login.failure", null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Database integrity violation: {}", ex.getMessage());
        String message = "Database integrity violation. Please check your input data.";

        // Check for specific database errors
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("duplicate key") || ex.getMessage().contains("Duplicate entry")) {
                message = "A record with this information already exists.";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot perform this operation due to related data constraints.";
            }
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, message, null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Invalid state: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);

        // Check if the exception message contains "not found" to return 404
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
        }

        // Add detailed error information for debugging
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("type", ex.getClass().getSimpleName());
        errorDetails.put("message", ex.getMessage());

        if (ex.getCause() != null) {
            errorDetails.put("cause", ex.getCause().getMessage());
        }

        // Otherwise return 500
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(false, "An unexpected error occurred. Please try again later.", errorDetails));
    }
}