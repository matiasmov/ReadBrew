package com.example.ReadBrew.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class ExternalBookItemDTO {
    private String id;
    private VolumeInfo volumeInfo; 

    @Data
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String description;
        private List<String> categories;
        private Integer pageCount;
        private ImageLinks imageLinks; 
    }

    @Data
    public static class ImageLinks {
        private String thumbnail;
    }
}