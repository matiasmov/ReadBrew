package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.RegisterDTO;
import com.example.ReadBrew.dto.UserResponseDTO;
import com.example.ReadBrew.model.Avatar;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.service.UserService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/v1/users") 
public class UserController {

    @Autowired 
    private UserService userService;

    private User getUserLoggedIn() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        User loggedInUser = getUserLoggedIn();
        UserResponseDTO profile = userService.getMyProfile(loggedInUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me/avatar/{avatarId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateMyAvatar(@PathVariable Long avatarId) {
        try {
            User loggedInUser = getUserLoggedIn();
            return ResponseEntity.ok(userService.equipAvatar(loggedInUser.getId(), avatarId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/avatars")
    public ResponseEntity<List<Avatar>> listAvatars() {
        return ResponseEntity.ok(userService.getAllAvailableAvatars());
    }

   

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterDTO data) { 
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}