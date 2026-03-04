package com.example.ReadBrew.config;

import com.example.ReadBrew.model.Achievement;
import com.example.ReadBrew.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AchievementRepository achievementRepository;

    @Override
    public void run(String... args) throws Exception {

        //I need to check how the flyway works in order to implement it in the future '-'
        
        createOrUpdateAchievement("First Sip", "Finished your first reading.", 
            "/images/badges/temp_first.png", null, "TOTAL_BOOKS", null, 1);

        createOrUpdateAchievement("Avid Reader", "You read 5 books!", 
            "/images/badges/temp_reader.png", "/images/avatars/temp_vet.png", "TOTAL_BOOKS", null, 5);

        createOrUpdateAchievement("Night Owl", "Finished reading late at night.", 
            "/images/badges/temp_owl.png", "/images/avatars/temp_owl.png", "READ_TIME", "LATE_NIGHT", 1);

        createOrUpdateAchievement("Influencer", "You reached 10 followers!", 
            "/images/badges/temp_star.png", "/images/avatars/star.png", "TOTAL_FOLLOWERS", null, 10);
            
        System.out.println("DataSeeder: Achievements synchronized successfully!");
    }

 
    private void createOrUpdateAchievement(String title, String description, String imageUrl, 
                                   String rewardAvatarUrl, String metricType, 
                                   String metricDetail, int targetValue) {
        
        
        Achievement badge = achievementRepository.findByTitle(title).orElse(new Achievement());
        
      
        badge.setTitle(title);
        badge.setDescription(description);
        badge.setImageUrl(imageUrl);
        badge.setRewardAvatarUrl(rewardAvatarUrl);
        badge.setMetricType(metricType);
        badge.setMetricDetail(metricDetail);
        badge.setTargetValue(targetValue);
        
        achievementRepository.save(badge);
    }
}