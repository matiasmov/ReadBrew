package com.example.ReadBrew.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedResponseDTO {
    private Long eventId;
    private String eventType;
    private LocalDateTime createdAt;
    
    private String username;
    private String userAvatarUrl;
    
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
    
    private String coffeeName;
    private String coffeeIconUrl;
    
    private Integer rating;
    private String reviewText;
    
    private String achievementTitle;
    private String achievementIconUrl;
}