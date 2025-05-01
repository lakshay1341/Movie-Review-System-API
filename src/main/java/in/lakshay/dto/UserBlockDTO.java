package in.lakshay.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBlockDTO {
    private Long id;
    private Long blockedUserId;
    private String blockedUserName;
    private Long blockedById;
    private String blockedByName;
    private String reason;
    private LocalDateTime blockedAt;
    private boolean isAdminBlock;
}
