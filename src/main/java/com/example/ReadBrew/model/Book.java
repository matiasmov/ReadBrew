package com.example.ReadBrew.model;
import jakarta.persistence.Column;
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

    @Column(unique = true)
    private String externalId;

    @Column(unique = true)
    private String isbn;

    private String title;
    
    private String author;
    
    private String genre;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String pixelArtUrl;

    
}
