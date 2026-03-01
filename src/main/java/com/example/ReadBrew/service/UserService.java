package com.example.ReadBrew.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.RegisterDTO;
import com.example.ReadBrew.dto.UserResponseDTO;
import com.example.ReadBrew.model.Avatar;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.AvatarRepository;
import com.example.ReadBrew.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(RegisterDTO data) {
        if (userRepository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Email already registered");
        }

        User newUser = new User();
        newUser.setUsername(data.username());
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setLevel(1);
        newUser.setXp(0);

        User savedUser = userRepository.save(newUser);
        return toDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO equipAvatar(Long userId, Long avatarId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new RuntimeException("Avatar not found"));

        if (user.getLevel() < avatar.getMinLevelRequired()) {
            throw new RuntimeException("Insufficient level: " + avatar.getMinLevelRequired() + " required");
        }

        user.setProfileAvatar(avatar);
        return toDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Avatar> getAllAvailableAvatars() {
    return avatarRepository.findAll();
}

   
    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getLevel(), 
            user.getXp(), 
            user.getProfileAvatar()
        );
    }

    public UserResponseDTO getMyProfile(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));
    return toDTO(user);
}
}