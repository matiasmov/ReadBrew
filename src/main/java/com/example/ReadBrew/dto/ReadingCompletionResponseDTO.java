package com.example.ReadBrew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingCompletionResponseDTO {

    private Long id;
    private String status;
    private LocalDateTime finishedAt;

    private String bookTitle;
    private String bookAuthor;
   
    private int xpGained;
    private int currentXp;
    private int currentLevel;
    private boolean levelUp;

   
    private String coffeeName;
    private boolean likedCoffee;
    private Integer bookRating;
    private String review;

  
    private Long totalPagesReadSoFar; 
}