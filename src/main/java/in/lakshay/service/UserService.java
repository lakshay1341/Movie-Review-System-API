package in.lakshay.service;

import in.lakshay.dto.UserAdminDTO;
import in.lakshay.dto.UserDTO;
import in.lakshay.entity.Role;
import in.lakshay.entity.User;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.RoleRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Get all users (admin only)
     */
    public Page<UserAdminDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToAdminDTO);
    }

    /**
     * Get user by ID (admin or self)
     */
    public UserAdminDTO getUserById(Long id, String currentUsername) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if the current user is requesting their own data or is an admin
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        
        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");
        boolean isSelf = currentUser.getId().equals(id);
        
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You don't have permission to access this user's data");
        }

        return convertToAdminDTO(user);
    }

    /**
     * Update user (admin or self)
     */
    @Transactional
    public UserAdminDTO updateUser(Long id, UserDTO userDTO, String currentUsername) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if the current user is updating their own data or is an admin
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        
        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");
        boolean isSelf = currentUser.getId().equals(id);
        
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        // Update basic user information
        if (userDTO.getUserName() != null && !userDTO.getUserName().isEmpty()) {
            // Check if username is already taken by another user
            Optional<User> existingUser = userRepository.findByUserName(userDTO.getUserName());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUserName(userDTO.getUserName());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(userDTO.getEmail());
        }

        // Save the updated user
        User updatedUser = userRepository.save(user);
        return convertToAdminDTO(updatedUser);
    }

    /**
     * Update user role (admin only)
     */
    @Transactional
    public UserAdminDTO updateUserRole(Long userId, Long roleId) {
        log.info("Updating role for user with id: {} to role id: {}", userId, roleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return convertToAdminDTO(updatedUser);
    }

    /**
     * Delete user (admin only)
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userRepository.delete(user);
    }

    /**
     * Update last login time
     */
    @Transactional
    public void updateLastLoginTime(String username) {
        log.info("Updating last login time for user: {}", username);
        // This method is now a no-op since we removed the lastLoginAt field
        // We'll implement this properly in the future when we add the field to the database
    }

    /**
     * Search users by username or email
     */
    public Page<UserAdminDTO> searchUsers(String query, Pageable pageable) {
        log.info("Searching users with query: {}", query);
        Page<User> users = userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable);
        return users.map(this::convertToAdminDTO);
    }

    /**
     * Count active users
     */
    public Long countActiveUsers() {
        log.info("Counting active users");
        // This method now returns the total number of users since we don't have the active field
        return (long) userRepository.findAll().size();
    }

    /**
     * Count new users for a date range
     */
    public Long countNewUsersForDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Counting new users for date range: {} to {}", startDateTime, endDateTime);
        // This method now returns 0 since we don't have the createdAt field
        // We'll implement this properly in the future when we add the field to the database
        return 0L;
    }

    /**
     * Convert User entity to UserAdminDTO
     */
    private UserAdminDTO convertToAdminDTO(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getName());
        dto.setReservationCount(user.getReservations().size());
        dto.setReviewCount(user.getReviews().size());
        return dto;
    }
}
