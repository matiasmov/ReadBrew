package com.example.ReadBrew.dto;

import java.util.List;

public class BookResponseDTO {
    private String id;
    private String title;
    private List<String> authors;
    private String description;
    private List<String> categories;

    // Error with lombok idk why :(
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}