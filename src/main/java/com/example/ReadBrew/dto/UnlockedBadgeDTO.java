package com.example.ReadBrew.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UnlockedBadgeDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime unlockedAt; 
}