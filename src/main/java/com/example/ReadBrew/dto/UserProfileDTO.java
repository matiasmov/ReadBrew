package com.example.ReadBrew.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

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
    private int followersCount;
    private int followingCount;
}