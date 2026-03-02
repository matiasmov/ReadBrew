package com.example.ReadBrew.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;
    
    private String rewardAvatarUrl; 

    private String metricType;   
    private String metricDetail; 
    private int targetValue;     
}