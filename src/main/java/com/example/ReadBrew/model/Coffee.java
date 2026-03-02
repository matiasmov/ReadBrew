package com.example.ReadBrew.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "coffees")
@Data
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; 

    @Column(columnDefinition = "TEXT")
    private String description; 

    private String pixelArtUrl; 

    @Column(columnDefinition = "TEXT")
    private String recipe;

    @ManyToMany
    @JoinTable(
        name = "coffee_tags",
        joinColumns = @JoinColumn(name = "coffee_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @EqualsAndHashCode.Exclude 
    @ToString.Exclude          
    private Set<Tag> tags = new HashSet<>();
}