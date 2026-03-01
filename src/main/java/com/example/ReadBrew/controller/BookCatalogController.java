package com.example.ReadBrew.controller;

import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.BookResponseDTO; 
import com.example.ReadBrew.service.BookCatalogService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/catalog")
@Validated
public class BookCatalogController {

    @Autowired
    private BookCatalogService bookCatalogService;

    @GetMapping("/search")
    public List<BookResponseDTO> searchBooks( 
            @RequestParam @NotBlank(message = "The search term cannot be empty") String title) {
        
       
        return bookCatalogService.searchBooks(title);
    }
}