package com.example.ReadBrew.dto;

import lombok.Data;

@Data
public class CompleteReadingDTO {
    private Long coffeeId;
    private boolean likedCoffee;
    private Integer bookRating;  
    private String review;       
}