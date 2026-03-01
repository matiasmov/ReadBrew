package com.example.ReadBrew.model;

import java.time.LocalDateTime;

import com.example.ReadBrew.model.enums.ReadingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reading_diaries")
@Data

public class ReadingDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"email", "password", "id"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "coffee_id") 
    private Coffee coffee;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status; 

    private boolean coffeeConsumed = false; 

    private Boolean likedCoffee; 

    private Integer bookRating; 

    @Column(columnDefinition = "TEXT") 
    private String review;

    private LocalDateTime finishedAt; 

    @ManyToOne
    @JoinColumn(name = "recommended_coffee_id") 
    private Coffee recommendedCoffee;

     // Lombok error again, i need this :( 

    public Coffee getRecommendedCoffee() {
        return recommendedCoffee;
    }

    public void setRecommendedCoffee(Coffee recommendedCoffee) {
        this.recommendedCoffee = recommendedCoffee;
    }

    public ReadingStatus getStatus() {
        return status;
    }
   
    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

}