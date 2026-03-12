package com.example.ReadBrew.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterDTO(
        
        @NotBlank(message = "O nome de usuário não pode ficar vazio!") 
        @Pattern(
        
            regexp = "^[a-zA-Z0-9_]{3,20}$", 
            message = "O apelido deve ter de 3 a 20 caracteres e usar apenas letras, números e sublinhados (sem emojis ou espaços!)."
        )
        String username, 
        
        @NotBlank(message = "O e-mail é obrigatório!") 
        @Email(message = "O formato do e-mail é inválido.") 
        String email, 
        
        @NotBlank(message = "A senha é obrigatória!")
        @Pattern(
         
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$", 
            message = "A senha deve ter no mínimo 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial."
        )
        String password
) {}