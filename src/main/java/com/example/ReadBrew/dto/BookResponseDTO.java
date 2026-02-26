package com.example.ReadBrew.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private String id;
    private String title;
    private List<String> authors;
    private String description;
    private List<String> categories;
    private String thumbnailUrl; 
    private Integer pageCount;  
}