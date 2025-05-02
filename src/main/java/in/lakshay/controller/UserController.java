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
@RequestMapping("/api/v1/users") // base path for user endpoints
@Slf4j // for logging
@Tag(name = "User Management", description = "User management APIs") // swagger docs
public class UserController {

    @Autowired // TODO: switch to constructor injection
    private UserService userService; // handles user business logic

    @Autowired
    private MessageSource messageSource; // i18n

    @GetMapping // get all users
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @RateLimiter(name = "basic") // prevent abuse
    @Operation(summary = "Get all users", description = "Returns all users (Admin only)")
    public ResponseEntity<ApiResponse<Page<UserAdminDTO>>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String search) {

        log.info("Fetching all users with search: {}", search);

        // if search param provided, search users, otherwise get all
        Page<UserAdminDTO> users = (search != null && !search.isEmpty())
                ? userService.searchUsers(search, pageable)
                : userService.getAllUsers(pageable);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("users.retrieved.success", null, LocaleContextHolder.getLocale()),
                users
        ));
    }

    @GetMapping("/{id}") // get user by id
    @PreAuthorize("isAuthenticated()") // must be logged in
    @RateLimiter(name = "basic")
    @Operation(summary = "Get user by ID", description = "Returns a user by ID (Admin or self)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> getUserById(@PathVariable Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching user with id: {} by user: {}", id, currentUsername);

        // this will check if user is admin or self - throws 403 if not
        UserAdminDTO user = userService.getUserById(id, currentUsername);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.retrieved.success", null, LocaleContextHolder.getLocale()),
                user
        ));
    }

    @PutMapping("/{id}") // update user profile
    @PreAuthorize("isAuthenticated()") // must be logged in
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user", description = "Updates a user (Admin or self)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Updating user with id: {} by user: {}", id, currentUsername);

        try {
            // this will check if user is admin or self - throws 403 if not
            UserAdminDTO updatedUser = userService.updateUser(id, userDTO, currentUsername);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    messageSource.getMessage("user.updated.success", null, LocaleContextHolder.getLocale()),
                    updatedUser
            ));
        } catch (IllegalArgumentException e) {
            // validation failed
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

    @PutMapping("/{id}/role") // change user role
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user role", description = "Updates a user's role (Admin only)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {

        // extract roleId from request
        Long roleId = request.get("roleId");
        if (roleId == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Role ID is required", // simple error msg
                    null
            ));
        }

        log.info("Updating role for user with id: {} to role id: {}", id, roleId);

        // update the user's role
        UserAdminDTO updatedUser = userService.updateUserRole(id, roleId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.role.updated.success", null, LocaleContextHolder.getLocale()),
                updatedUser
        ));
    }

    @PutMapping("/{id}/status") // activate/deactivate user
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @RateLimiter(name = "basic")
    @Operation(summary = "Update user status", description = "Activates or deactivates a user (Admin only)")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        // extract active status from request
        Boolean active = request.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Active status is required", // simple error msg
                    null
            ));
        }

        log.info("Updating status for user with id: {} to active: {}", id, active);

        // update user status - empty DTO means only update status
        UserAdminDTO updatedUser = userService.updateUser(id, new UserDTO(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        // use different message based on active status
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

    @DeleteMapping("/{id}") // delete user
    @PreAuthorize("hasRole('ROLE_ADMIN')") // admin only
    @RateLimiter(name = "basic")
    @Operation(summary = "Delete user", description = "Deletes a user (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);

        // this will check if user exists and delete them
        userService.deleteUser(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                messageSource.getMessage("user.deleted.success", null, LocaleContextHolder.getLocale()),
                null
        ));
    }
} // end of UserController
