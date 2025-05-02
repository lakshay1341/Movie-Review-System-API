package in.lakshay.dto;

import lombok.Data;

import java.time.LocalDateTime;

// user block info for moderation
@Data
public class UserBlockDTO {
    private Long id;  // pk

    // who got blocked
    private Long blockedUserId;
    private String blockedUserName;

    // who did the blocking
    private Long blockedById;
    private String blockedByName;

    private String reason;  // why they were blocked
    private LocalDateTime blockedAt;  // when
    private boolean isAdminBlock;  // admin or user block

    // TODO: maybe add duration/expiry?
}
