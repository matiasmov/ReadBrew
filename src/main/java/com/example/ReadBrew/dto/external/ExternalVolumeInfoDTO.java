package com.example.ReadBrew.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class ExternalVolumeInfoDTO {
    private String title;
    private List<String> authors;
    private String description;
    private List<String> categories;
}