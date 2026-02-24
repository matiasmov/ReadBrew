package com.example.ReadBrew.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String username;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;


    @NotBlank (message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).*$",
        message = "A senha deve conter pelo menos uma letra maiúscula e um caractere especial"
    )
    private String password;
}