package com.example.ReadBrew.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category_tags")
@Data
public class CategoryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "category_name", nullable = false)
    private String categoryName; 

   
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}