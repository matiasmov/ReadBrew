package com.example.ReadBrew.dto;

import lombok.Data;

@Data
public class CompleteReadingDTO {
    
    private boolean likedCoffee;
    private Integer bookRating;  
    private String review;       
}