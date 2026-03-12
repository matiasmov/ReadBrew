package com.example.ReadBrew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.AuthenticationDTO;
import com.example.ReadBrew.dto.RegisterDTO;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.UserRepository;
import com.example.ReadBrew.security.TokenService;
import com.example.ReadBrew.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService; 

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data) {
        
        User user = userRepository.findByEmail(data.email());

        // check if the blocking time has elapsed
        if (user != null) {
            userService.unlockWhenTimeExpired(user);
        }

        try {
            
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

          // reset attempts
            if (user != null) {
                userService.resetFailedAttempts(user);
            }

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(token);

        } catch (LockedException e) {

            // blocked
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Conta bloqueada por excesso de tentativas. Aguarde 15 minutos e tente novamente.");
                    
        } catch (BadCredentialsException e) {
            
            // wrong password
            if (user != null) {
                userService.increaseFailedAttempts(user); // ++ attempts
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas!");
                    
        } catch (Exception e) {
            // Others erros
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro na autenticação.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        try {
            userService.createUser(data);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String errorMessage = error != null ? error.getDefaultMessage() : "Dados inválidos";
        return ResponseEntity.badRequest().body(errorMessage);
    }
}