package in.lakshay.service;

import in.lakshay.dto.UserBlockDTO;
import in.lakshay.entity.User;
import in.lakshay.entity.UserBlock;
import in.lakshay.exception.ResourceNotFoundException;
import in.lakshay.repo.UserBlockRepository;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBlockService {

    @Autowired
    private UserBlockRepository userBlockRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Block a user
     */
    @Transactional
    public UserBlockDTO blockUser(Long userIdToBlock, String reason, String blockerUsername, boolean isAdminBlock) {
        log.info("User {} blocking user {}, reason: {}, isAdminBlock: {}", blockerUsername, userIdToBlock, reason, isAdminBlock);
        
        // Check if the user to block exists
        User userToBlock = userRepository.findById(userIdToBlock)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userIdToBlock));
        
        // Check if the blocker exists
        User blocker = userRepository.findByUserName(blockerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + blockerUsername));
        
        // Check if the blocker is an admin if this is an admin block
        if (isAdminBlock && !blocker.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can create admin blocks");
        }
        
        // Check if the user is already blocked
        if (userBlockRepository.existsByBlockedUserAndBlockedBy(userToBlock, blocker)) {
            throw new IllegalStateException("User is already blocked");
        }
        
        // Create the block
        UserBlock block = new UserBlock();
        block.setBlockedUser(userToBlock);
        block.setBlockedBy(blocker);
        block.setReason(reason);
        block.setAdminBlock(isAdminBlock);
        
        UserBlock savedBlock = userBlockRepository.save(block);
        
        return mapToDTO(savedBlock);
    }
    
    /**
     * Unblock a user
     */
    @Transactional
    public void unblockUser(Long userIdToUnblock, String unblockerUsername) {
        log.info("User {} unblocking user {}", unblockerUsername, userIdToUnblock);
        
        // Check if the user to unblock exists
        User userToUnblock = userRepository.findById(userIdToUnblock)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userIdToUnblock));
        
        // Check if the unblocker exists
        User unblocker = userRepository.findByUserName(unblockerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + unblockerUsername));
        
        // Find the block
        UserBlock block = userBlockRepository.findByBlockedUserAndBlockedBy(userToUnblock, unblocker)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found"));
        
        // Check if the unblocker is an admin if this is an admin block
        if (block.isAdminBlock() && !unblocker.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can remove admin blocks");
        }
        
        // Remove the block
        userBlockRepository.delete(block);
    }
    
    /**
     * Get all users blocked by a user
     */
    public List<UserBlockDTO> getUsersBlockedByUser(String username) {
        log.info("Getting users blocked by {}", username);
        
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        List<UserBlock> blocks = userBlockRepository.findByBlockedBy(user);
        
        return blocks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all users who blocked a user
     */
    public List<UserBlockDTO> getUsersWhoBlockedUser(String username) {
        log.info("Getting users who blocked {}", username);
        
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        List<UserBlock> blocks = userBlockRepository.findByBlockedUser(user);
        
        return blocks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all admin blocks
     */
    public Page<UserBlockDTO> getAdminBlocks(Pageable pageable) {
        log.info("Getting all admin blocks");
        
        Page<UserBlock> blocks = userBlockRepository.findByIsAdminBlock(true, pageable);
        
        return blocks.map(this::mapToDTO);
    }
    
    /**
     * Check if a user is blocked by another user
     */
    public boolean isUserBlockedByUser(Long blockedUserId, String blockerUsername) {
        log.info("Checking if user {} is blocked by {}", blockedUserId, blockerUsername);
        
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + blockedUserId));
        
        User blocker = userRepository.findByUserName(blockerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + blockerUsername));
        
        return userBlockRepository.existsByBlockedUserAndBlockedBy(blockedUser, blocker);
    }
    
    /**
     * Check if a user is blocked by an admin
     */
    public boolean isUserBlockedByAdmin(Long userId) {
        log.info("Checking if user {} is blocked by an admin", userId);
        
        return userBlockRepository.isUserBlockedByAdmin(userId);
    }
    
    /**
     * Map UserBlock entity to DTO
     */
    private UserBlockDTO mapToDTO(UserBlock block) {
        UserBlockDTO dto = new UserBlockDTO();
        dto.setId(block.getId());
        dto.setBlockedUserId(block.getBlockedUser().getId());
        dto.setBlockedUserName(block.getBlockedUser().getUserName());
        dto.setBlockedById(block.getBlockedBy().getId());
        dto.setBlockedByName(block.getBlockedBy().getUserName());
        dto.setReason(block.getReason());
        dto.setBlockedAt(block.getBlockedAt());
        dto.setAdminBlock(block.isAdminBlock());
        return dto;
    }
}
