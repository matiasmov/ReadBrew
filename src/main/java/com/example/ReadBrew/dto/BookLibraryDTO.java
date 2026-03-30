package com.example.ReadBrew.dto;

import java.util.List;

public record BookLibraryDTO(
        Long id,
        String title,
        List<String> authors,
        String thumbnailUrl,
        String category,
        String description
) {
    public BookLibraryDTO {

        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            thumbnailUrl = "/images/books/default_cover.png";
        }

        if (description == null) {
            description = "";
        } else if (description.length() > 500) {
            description = description.substring(0, 500) + "...";
        }
    }
}