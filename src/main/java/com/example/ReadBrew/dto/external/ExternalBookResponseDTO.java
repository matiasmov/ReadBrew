package com.example.ReadBrew.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class ExternalBookResponseDTO {
    private List<ExternalBookItemDTO> items; 
    public List<ExternalBookItemDTO> getItems() { return items; }
    public void setItems(List<ExternalBookItemDTO> items) { this.items = items; }
}