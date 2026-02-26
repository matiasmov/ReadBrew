package com.example.ReadBrew.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

    @Entity
    @Table(name = "books")
    @Data
    public class Book {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String externalId; 

        @Column(unique = true)
        private String isbn;

        @Column(nullable = false)
        private String title;
        
        @ElementCollection 
        private List<String> authors; 
        
        @ElementCollection
        private List<String> categories; 

        @Column(columnDefinition = "TEXT")
        private String description;
        
        @Column(length = 500)
        private String thumbnailUrl; 

        @Column(length = 500)
        private String pixelArtUrl;

        private Integer pageCount; // I will use it as a retrospective, don't delete it
    }