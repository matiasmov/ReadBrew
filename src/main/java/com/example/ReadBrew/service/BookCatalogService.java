package com.example.ReadBrew.service;

import java.util.ArrayList;
import java.util.List;      

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.ReadBrew.client.BookProviderClient; 
import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.external.ExternalBookItemDTO;
import com.example.ReadBrew.dto.external.ExternalBookResponseDTO;

@Service
public class BookCatalogService {

    @Autowired
    private BookProviderClient bookProviderClient;

    @Value("${google.books.api.key}")
    private String apiKey;

   @Cacheable(value = "bookSearch", key = "#title")
    public List<BookResponseDTO> searchBooks(String title){
        
        
        ExternalBookResponseDTO response = bookProviderClient.searchBooks(title, apiKey);
        
        List<BookResponseDTO> cleanList = new ArrayList<>();

        
        if (response != null && response.getItems() != null) {
            for (ExternalBookItemDTO item : response.getItems()) {
                if (item.getVolumeInfo() != null) {
                    BookResponseDTO book = new BookResponseDTO();
                    book.setId(item.getId());
                    book.setTitle(item.getVolumeInfo().getTitle());
                    book.setAuthors(item.getVolumeInfo().getAuthors());
                    book.setDescription(item.getVolumeInfo().getDescription());
                    book.setCategories(item.getVolumeInfo().getCategories());
                    
                    cleanList.add(book);
                }
            }
        }
        return cleanList;
    }
}