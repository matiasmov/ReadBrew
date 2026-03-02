package com.example.ReadBrew.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponseDTO {
    private String id;
    private VolumeInfo volumeInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String description;
        private List<String> categories;
        private Integer pageCount;
        private ImageLinks imageLinks;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        private String thumbnail;
    }

    private void ensureVolumeInfo() {
        if (this.volumeInfo == null) this.volumeInfo = new VolumeInfo();
    }

    private void ensureImageLinks() {
        ensureVolumeInfo();
        if (this.volumeInfo.getImageLinks() == null) this.volumeInfo.setImageLinks(new ImageLinks());
    }


    public void setTitle(String title) { ensureVolumeInfo(); this.volumeInfo.setTitle(title); }
    public void setAuthors(List<String> authors) { ensureVolumeInfo(); this.volumeInfo.setAuthors(authors); }
    public void setDescription(String description) { ensureVolumeInfo(); this.volumeInfo.setDescription(description); }
    public void setCategories(List<String> categories) { ensureVolumeInfo(); this.volumeInfo.setCategories(categories); }
    public void setPageCount(Integer pageCount) { ensureVolumeInfo(); this.volumeInfo.setPageCount(pageCount); }
    

    public void setThumbnailUrl(String url) { 
        ensureImageLinks(); 
        this.volumeInfo.getImageLinks().setThumbnail(url); 
    }

  

    public String getTitle() { return volumeInfo != null ? volumeInfo.getTitle() : null; }
    public List<String> getAuthors() { return volumeInfo != null ? volumeInfo.getAuthors() : null; }
    public String getDescription() { return volumeInfo != null ? volumeInfo.getDescription() : null; }
    public List<String> getCategories() { return volumeInfo != null ? volumeInfo.getCategories() : null; }
    public Integer getPageCount() { return volumeInfo != null ? volumeInfo.getPageCount() : null; }
    
    public String getThumbnailUrl() {
        if (volumeInfo != null && volumeInfo.getImageLinks() != null) {
            String thumb = volumeInfo.getImageLinks().getThumbnail();
            return (thumb != null) ? thumb.replace("http://", "https://") : null;
        }
        return null;
    }
}