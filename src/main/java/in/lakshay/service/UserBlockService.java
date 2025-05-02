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

    // main method to block users - works for both admin and regular blocks
    @Transactional
    public UserBlockDTO blockUser(Long userIdToBlock, String reason, String blockerUsername, boolean isAdminBlock) {
        log.info("User {} blocking user {}, reason: {}, isAdminBlock: {}", blockerUsername, userIdToBlock, reason, isAdminBlock); // log it

        // Check if the user to block exists
        User userToBlock = userRepository.findById(userIdToBlock)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userIdToBlock));

        // Check if the blocker exists
        User blocker = userRepository.findByUserName(blockerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + blockerUsername));

        // Check if the blocker is an admin if this is an admin block
        // prevent regular users from creating admin blocks
        if (isAdminBlock && !blocker.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can create admin blocks"); // nice try buddy
        }

        // Check if the user is already blocked
        // no need to create duplicate blocks
        if (userBlockRepository.existsByBlockedUserAndBlockedBy(userToBlock, blocker)) {
            throw new IllegalStateException("User is already blocked"); // already done
        }

        // Create the block
        UserBlock block = new UserBlock(); // new entity
        block.setBlockedUser(userToBlock); // who's getting blocked
        block.setBlockedBy(blocker); // who did the blocking
        block.setReason(reason); // why they're blocked
        block.setAdminBlock(isAdminBlock); // admin or regular block

        UserBlock savedBlock = userBlockRepository.save(block);

        return mapToDTO(savedBlock);
    }

    // remove a block - undo the block
    @Transactional
    public void unblockUser(Long userIdToUnblock, String unblockerUsername) {
        log.info("User {} unblocking user {}", unblockerUsername, userIdToUnblock); // track who's doing what

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
        // regular users can't remove admin blocks - that would defeat the purpose!
        if (block.isAdminBlock() && !unblocker.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can remove admin blocks"); // security check
        }

        // Remove the block
        userBlockRepository.delete(block);
    }

    // get list of users that this user has blocked
    // used in user profile/settings
    public List<UserBlockDTO> getUsersBlockedByUser(String username) {
        log.info("Getting users blocked by {}", username); // debug

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<UserBlock> blocks = userBlockRepository.findByBlockedBy(user);

        return blocks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // who blocked me? - not currently used in UI but might be useful
    // maybe for admin reports
    public List<UserBlockDTO> getUsersWhoBlockedUser(String username) {
        log.info("Getting users who blocked {}", username); // for debugging

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<UserBlock> blocks = userBlockRepository.findByBlockedUser(user);

        return blocks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // admin dashboard - show all admin blocks with pagination
    // only admins should call this
    public Page<UserBlockDTO> getAdminBlocks(Pageable pageable) {
        log.info("Getting all admin blocks"); // log it

        Page<UserBlock> blocks = userBlockRepository.findByIsAdminBlock(true, pageable);

        return blocks.map(this::mapToDTO);
    }

    // quick check if user A blocked user B
    // used when showing content/interactions
    public boolean isUserBlockedByUser(Long blockedUserId, String blockerUsername) {
        log.info("Checking if user {} is blocked by {}", blockedUserId, blockerUsername); // trace

        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + blockedUserId));

        User blocker = userRepository.findByUserName(blockerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + blockerUsername));

        return userBlockRepository.existsByBlockedUserAndBlockedBy(blockedUser, blocker);
    }

    // check if ANY admin has blocked this user
    // important for auth checks - admin-blocked users can't do much
    public boolean isUserBlockedByAdmin(Long userId) {
        log.info("Checking if user {} is blocked by an admin", userId); // for debugging

        return userBlockRepository.isUserBlockedByAdmin(userId);
    }

    // convert db entity to dto for api responses
    // standard mapper method
    private UserBlockDTO mapToDTO(UserBlock block) { // nothing fancy
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
