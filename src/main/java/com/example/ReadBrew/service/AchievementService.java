package com.example.ReadBrew.service;

import com.example.ReadBrew.model.Achievement;
import com.example.ReadBrew.model.ReadingDiary;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.model.UserAchievement;
import com.example.ReadBrew.repository.AchievementRepository;
import com.example.ReadBrew.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    public void checkAndUnlockAchievements(User user, ReadingDiary justFinishedDiary, long totalBooksRead, List<String> userGenres) {
        
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement badge : allAchievements) {
            
            if (userAchievementRepository.existsByUserAndAchievement(user, badge)) {
                continue;
            }

            boolean unlocked = false;

            //Here I've outlined some criteria for the achievements
          
            if ("TOTAL_BOOKS".equals(badge.getMetricType())) {
                if (totalBooksRead >= badge.getTargetValue()) {
                    unlocked = true;
                }
            } 
            
          
            else if ("READ_GENRE".equals(badge.getMetricType())) {
                long genreCount = userGenres.stream()
                        .filter(g -> g.equalsIgnoreCase(badge.getMetricDetail()))
                        .count();
                if (genreCount >= badge.getTargetValue()) {
                    unlocked = true;
                }
            }
            
            
            else if ("READ_TIME".equals(badge.getMetricType())) {
                if ("LATE_NIGHT".equals(badge.getMetricDetail())) {
                   
                    int hour = justFinishedDiary.getFinishedAt().getHour();
                    
                    if (hour >= 0 && hour <= 4) { 
                        unlocked = true;
                    }
                }
            }

           
            else if ("TOTAL_FOLLOWERS".equals(badge.getMetricType())) {
                
                if (user.getFollowers().size() >= badge.getTargetValue()) {
                    unlocked = true;
                }
            }

           
            if (unlocked) {
                UserAchievement newBadge = new UserAchievement();
                newBadge.setUser(user);
                newBadge.setAchievement(badge);
                newBadge.setUnlockedAt(LocalDateTime.now());
                userAchievementRepository.save(newBadge);
            }
        }
    }
}