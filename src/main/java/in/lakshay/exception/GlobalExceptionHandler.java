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
import java.util.Map; // for error details

@RestControllerAdvice // catches exceptions from all controllers
@Slf4j // logging
public class GlobalExceptionHandler {
    // handles all the exceptions thrown by our controllers
    // makes sure we return nice error msgs to clients

    // handles rate limiting - when users are spamming our api
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitExceededException(RequestNotPermitted ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage()); // just warn level, not error
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS) // 429 status
            .body(new ApiResponse<>(false, "system.rate.limit.exceeded", null)); // i18n key
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // when validation fails - bad input data
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

    // when user doesn't exist
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage()); // don't expose too much info in response
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>(false, "auth.login.failure", null));
    }

    // db constraint violations - unique keys, fk, etc
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Database integrity violation: {}", ex.getMessage());
        String message = "Database integrity violation. Please check your input data."; // default msg

        // try to give more specific error msgs based on the exception
        // this is a bit hacky but works ok for mysql/postgres
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("duplicate key") || ex.getMessage().contains("Duplicate entry")) {
                message = "A record with this information already exists."; // unique constraint
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot perform this operation due to related data constraints."; // FK violation
            }
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST) // 400 status
            .body(new ApiResponse<>(false, message, null));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // when app is in wrong state for operation
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Invalid state: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(false, ex.getMessage(), null));
    }


    // catch-all for any unhandled exceptions
    // last line of defense against weird errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex); // log full stack trace

        // special case - if msg has "not found", return 404 instead of 500
        // helps with some spring exceptions that aren't caught by our ResourceNotFoundException
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new ApiResponse<>(false, ex.getMessage(), null));
        }

        // add details for debugging - only shown in dev/test envs
        // todo: hide these details in prod
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("type", ex.getClass().getSimpleName()); // exception class
        errorDetails.put("message", ex.getMessage()); // error msg

        if (ex.getCause() != null) {
            errorDetails.put("cause", ex.getCause().getMessage()); // root cause if available
        }

        // generic 500 error - don't expose too much to client
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
            .body(new ApiResponse<>(false, "An unexpected error occurred. Please try again later.", errorDetails));
    }
}