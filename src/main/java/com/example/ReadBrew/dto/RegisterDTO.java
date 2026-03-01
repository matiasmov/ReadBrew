package com.example.ReadBrew.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterDTO(
        @NotBlank(message = "The user cannot be empty") 
        String username, 
        
        @NotBlank(message = "An email is required!") 
        @Email(message = "Invalid email format") 
        String email, 
        
        @NotBlank(message = "A password is required!")
        @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).{8,}$", 
            message = "A minimum of 8 characters, including one uppercase letter, one number, and one special character."
        )
        String password
) {}