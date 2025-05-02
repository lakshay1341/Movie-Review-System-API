package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.UserBlockDTO;
import in.lakshay.dto.UserBlockRequest;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.service.UserBlockService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-blocks") // base path for user block endpoints
@Tag(name = "User Blocking", description = "APIs for managing user blocks") // swagger docs
@Slf4j // logging
public class UserBlockController {

    @Autowired // TODO: switch to constructor injection
    private UserBlockService userBlockService; // handles user blocking logic

    @Autowired
    private MessageSource messageSource; // i18n

    @RateLimiter(name = "basic") // prevent abuse
    @PostMapping // block a user
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Block a user", description = "Blocks a user from interacting with your content")
    public ResponseEntity<ApiResponse<UserBlockDTO>> blockUser(@Valid @RequestBody UserBlockRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} attempting to block user ID: {}", username, request.getUserId());

        try {
            // false = not an admin block
            UserBlockDTO block = userBlockService.blockUser(request.getUserId(), request.getReason(), username, false);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "user.blocked.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    block
            ));
        } catch (ResourceNotFoundException e) {
            // user to block not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (IllegalStateException e) {
            // already blocked or trying to block self
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            // something else went wrong
            log.error("Error blocking user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @RateLimiter(name = "basic")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin block a user", description = "Blocks a user by an admin (Admin only)")
    public ResponseEntity<ApiResponse<UserBlockDTO>> adminBlockUser(@Valid @RequestBody UserBlockRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            UserBlockDTO block = userBlockService.blockUser(request.getUserId(), request.getReason(), username, true);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "user.admin.blocked.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    block
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @RateLimiter(name = "basic")
    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Unblock a user", description = "Unblocks a previously blocked user")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable Long userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userBlockService.unblockUser(userId, username);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "user.unblocked.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @RateLimiter(name = "basic")
    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin unblock a user", description = "Unblocks a user who was blocked by an admin (Admin only)")
    public ResponseEntity<ApiResponse<Void>> adminUnblockUser(@PathVariable Long userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userBlockService.unblockUser(userId, username);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage(
                            "user.admin.unblocked.success",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @RateLimiter(name = "basic")
    @GetMapping("/my-blocks") // get users I've blocked
    @PreAuthorize("isAuthenticated()") // must be logged in
    @Operation(summary = "Get my blocks", description = "Returns all users blocked by the current user")
    public ResponseEntity<ApiResponse<List<UserBlockDTO>>> getMyBlocks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} fetching their blocks", username);

        // get all users blocked by current user
        List<UserBlockDTO> blocks = userBlockService.getUsersBlockedByUser(username);
        log.info("Found {} blocks for user {}", blocks.size(), username);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "user.blocks.retrieved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                blocks
        ));
    }

    @RateLimiter(name = "basic")
    @GetMapping("/admin") // get admin blocks
    @PreAuthorize("hasRole('ADMIN')") // admin only
    @Operation(summary = "Get admin blocks", description = "Returns all users blocked by admins (Admin only)")
    public ResponseEntity<ApiResponse<Page<UserBlockDTO>>> getAdminBlocks(
            @PageableDefault(size = 10) Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Admin {} fetching admin blocks", username);

        // get all users blocked by any admin
        Page<UserBlockDTO> blocks = userBlockService.getAdminBlocks(pageable);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        "user.admin.blocks.retrieved.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                blocks
        ));
    }
} // end of UserBlockController
