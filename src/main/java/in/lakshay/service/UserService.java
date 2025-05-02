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

// handles user management - CRUD operations, search, etc.

@Service
@Slf4j // logging
public class UserService {

    @Autowired // todo: switch to constructor injection
    private UserRepository userRepository; // user data access

    @Autowired
    private RoleRepository roleRepository; // role data access

    @Autowired
    private PasswordEncoder passwordEncoder; // for hashing passwords

    @Autowired
    private ModelMapper modelMapper; // entity <-> dto conversion

    // get all users - admin dashboard feature
    public Page<UserAdminDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToAdminDTO); // convert to admin DTOs
    }

    // get user by ID - with permission check (only self or admin)
    public UserAdminDTO getUserById(Long id, String currentUsername) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // security check - only admins or the user themselves can access
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You don't have permission to access this user's data"); // security!
        }

        return convertToAdminDTO(user); // convert to DTO
    }

    // update user profile - with permission check
    @Transactional
    public UserAdminDTO updateUser(Long id, UserDTO userDTO, String currentUsername) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // security check - only admins or the user themselves can update
        User currentUser = userRepository.findByUserName(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You don't have permission to update this user"); // no hacking!
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

    // change user role - admin only operation
    @Transactional
    public UserAdminDTO updateUserRole(Long userId, Long roleId) {
        log.info("Updating role for user with id: {} to role id: {}", userId, roleId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        user.setRole(role); // set new role
        User updatedUser = userRepository.save(user);
        return convertToAdminDTO(updatedUser); // convert to DTO
    }

    // delete user account - admin only
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user); // bye bye user
    }

    // update last login timestamp
    @Transactional
    public void updateLastLoginTime(String username) {
        log.info("Updating last login time for user: {}", username);
        // TODO: this is a stub - we removed lastLoginAt field
        // will implement later when we add the field back
    }

    // search users by username or email - case insensitive
    public Page<UserAdminDTO> searchUsers(String query, Pageable pageable) {
        log.info("Searching users with query: {}", query);
        Page<User> users = userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable); // search both fields
        return users.map(this::convertToAdminDTO); // convert results
    }

    // count active users - for dashboard stats
    public Long countActiveUsers() {
        log.info("Counting active users");
        // FIXME: this is a hack - we don't have an active field yet
        // just returns total users for now
        return (long) userRepository.findAll().size();
    }

    // count new users in date range - for reports
    public Long countNewUsersForDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Counting new users for date range: {} to {}", startDateTime, endDateTime);
        // TODO: stub implementation - we don't track createdAt yet
        // will fix when we add the field
        return 0L; // fake data for now
    }

    // helper to convert User -> UserAdminDTO
    // manual mapping cuz it's simpler than configuring ModelMapper
    private UserAdminDTO convertToAdminDTO(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getName());
        dto.setReservationCount(user.getReservations().size()); // count of reservations
        dto.setReviewCount(user.getReviews().size()); // count of reviews
        return dto;
    }
}
