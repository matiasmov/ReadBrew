package com.example.ReadBrew.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompleteReadingDTO {
    
    private boolean likedCoffee;
    private Integer bookRating;  
    
    @Size(max = 1000, message = "A resenha deve ter no máximo 1000 caracteres.")
    private String review;     
}