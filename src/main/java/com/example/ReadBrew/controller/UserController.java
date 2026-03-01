package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.RegisterDTO; 
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.UserRepository;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/v1/users") 
public class UserController {

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody RegisterDTO data) { 
        
  
        if (userRepository.findByEmail(data.email()) != null) { 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("This email address is already registered");
        }
        
        User newUser = new User();
        newUser.setUsername(data.username()); 
        newUser.setEmail(data.email());       

        String hashedPassword = passwordEncoder.encode(data.password()); 
        newUser.setPassword(hashedPassword); 

        newUser.setXp(0);
        newUser.setLevel(1);

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}