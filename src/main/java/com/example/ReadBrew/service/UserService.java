package com.example.ReadBrew.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.RegisterDTO;
import com.example.ReadBrew.dto.UnlockedBadgeDTO;
import com.example.ReadBrew.dto.UserProfileDTO;
import com.example.ReadBrew.dto.UserResponseDTO;
import com.example.ReadBrew.dto.UserStatsDTO;           
import com.example.ReadBrew.model.Avatar;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.model.UserAchievement;      
import com.example.ReadBrew.repository.AvatarRepository;
import com.example.ReadBrew.repository.UserAchievementRepository;
import com.example.ReadBrew.repository.UserRepository; 

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReadingDiaryService readingDiaryService;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Transactional
    public UserResponseDTO createUser(RegisterDTO data) {
        if (userRepository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Email already registered");
        }

        User newUser = new User();
        newUser.setNickname(data.username());
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
            user.getNickname(), 
            user.getEmail(), 
            user.getLevel(), 
            user.getXp(), 
            user.getProfileAvatar(),
            user.getFollowers() != null ? user.getFollowers().size() : 0,
            user.getFollowing() != null ? user.getFollowing().size() : 0 
        );
    }

    @Transactional
    public void followUser(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("It is not possible to follow oneself.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found."));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found."));

        targetUser.getFollowers().add(currentUser);
        userRepository.save(targetUser); 
    }

    @Transactional
    public void unfollowUser(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow();
        User targetUser = userRepository.findById(targetUserId).orElseThrow();

        targetUser.getFollowers().remove(currentUser);
        userRepository.save(targetUser);
    }

    public UserProfileDTO getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        UserStatsDTO stats = readingDiaryService.getUserStats(userId);

        List<UserAchievement> userBadges = userAchievementRepository.findByUserId(userId);

        List<UnlockedBadgeDTO> badgesDTO = userBadges.stream()
                .map(ua -> UnlockedBadgeDTO.builder()
                        .id(ua.getAchievement().getId())
                        .title(ua.getAchievement().getTitle())
                        .description(ua.getAchievement().getDescription())
                        .imageUrl(ua.getAchievement().getImageUrl())
                        .unlockedAt(ua.getUnlockedAt())
                        .build())
                .collect(Collectors.toList());

        return UserProfileDTO.builder()
                .userId(user.getId())
                .username(user.getNickname())
                .avatarUrl(user.getProfileAvatar() != null ? user.getProfileAvatar().getFileName() : "default_user.png")
                .level(user.getLevel())
                .currentXp(user.getXp())
                .stats(stats)
                .badges(badgesDTO)
                .followersCount(user.getFollowers() != null ? user.getFollowers().size() : 0)
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .build();
    }

    public List<UserResponseDTO> searchUsersByName(String name) {
        return userRepository.findByNicknameContainingIgnoreCase(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final long LOCK_TIME_DURATION = 15; // minutes

    @Transactional
    public void increaseFailedAttempts(User user) {
        int newFailures = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailures);
        
        if (newFailures >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutTime(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLockoutTime(null);
        userRepository.save(user);
    }
    
    @Transactional
    public void unlockWhenTimeExpired(User user) {
        if (user.getLockoutTime() != null && LocalDateTime.now().isAfter(user.getLockoutTime())) {
            user.setLockoutTime(null);
            user.setFailedAttempts(0);
            userRepository.save(user);
        }
    }
}