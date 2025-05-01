package in.lakshay.controller;

import in.lakshay.dto.ApiResponse;
import in.lakshay.dto.UserAdminDTO;
import in.lakshay.dto.UserDTO;
import in.lakshay.service.UserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get all users", description = "Returns all users (Admin only)")
    public ResponseEntity<ApiResponse<Page<UserAdminDTO>>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String search) {
        
        log.info("Fetching all users with search: {}", search);
        
        Page<UserAdminDTO> users = (search != null && !search.isEmpty())
                ? userService.searchUsers(search, pageable)
                : userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("users.retrieved.success", null, LocaleContextHolder.getLocale()),
                users
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RateLimiter(name = "basic")
    @Operation(summary = "Get user by ID", description = "Returns a user by ID (Admin or self)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> getUserById(@PathVariable Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching user with id: {} by user: {}", id, currentUsername);
        
        UserAdminDTO user = userService.getUserById(id, currentUsername);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.retrieved.success", null, LocaleContextHolder.getLocale()),
                user
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user", description = "Updates a user (Admin or self)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Updating user with id: {} by user: {}", id, currentUsername);
        
        try {
            UserAdminDTO updatedUser = userService.updateUser(id, userDTO, currentUsername);
            
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("user.updated.success", null, LocaleContextHolder.getLocale()),
                    updatedUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user role", description = "Updates a user's role (Admin only)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        
        Long roleId = request.get("roleId");
        if (roleId == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Role ID is required",
                    null
            ));
        }
        
        log.info("Updating role for user with id: {} to role id: {}", id, roleId);
        
        UserAdminDTO updatedUser = userService.updateUserRole(id, roleId);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.role.updated.success", null, LocaleContextHolder.getLocale()),
                updatedUser
        ));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user status", description = "Activates or deactivates a user (Admin only)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        
        Boolean active = request.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Active status is required",
                    null
            ));
        }
        
        log.info("Updating status for user with id: {} to active: {}", id, active);
        
        UserAdminDTO updatedUser = userService.updateUser(id, new UserDTO(), SecurityContextHolder.getContext().getAuthentication().getName());
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage(
                        active ? "user.activated.success" : "user.deactivated.success",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                updatedUser
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RateLimiter(name = "basic")
    @Operation(summary = "Delete user", description = "Deletes a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.deleted.success", null, LocaleContextHolder.getLocale()),
                null
        ));
    }
}
