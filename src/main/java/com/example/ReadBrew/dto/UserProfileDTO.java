package com.example.ReadBrew.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private int level;
    private int currentXp;
    
    private UserStatsDTO stats;
    
    private List<UnlockedBadgeDTO> badges;
}