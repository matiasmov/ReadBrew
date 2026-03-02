package com.example.ReadBrew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingDiaryResponseDTO {
    private Long id;
    private String status;
    private String bookTitle;
    private String author;
    private String thumbnailUrl;
    private String userName;
    
    private String recommendedCoffee;
    private String recommendedCoffeeDescription;
    private String recommendedCoffeeRecipe;
    private String recommendedCoffeeImageUrl;
}