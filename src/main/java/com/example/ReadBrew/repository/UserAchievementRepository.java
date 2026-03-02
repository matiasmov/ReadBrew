package com.example.ReadBrew.repository;

import com.example.ReadBrew.model.Achievement;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    boolean existsByUserAndAchievement(User user, Achievement achievement);
    List<UserAchievement> findByUserId(Long userId);
}