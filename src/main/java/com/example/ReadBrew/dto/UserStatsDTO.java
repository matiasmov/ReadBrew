package com.example.ReadBrew.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private Long totalPagesRead;     
    private Integer booksCompleted;  
    private String favoriteCoffee;   
    private List<String> topGenres;  
}