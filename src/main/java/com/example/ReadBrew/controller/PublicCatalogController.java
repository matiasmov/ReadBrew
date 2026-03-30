package com.example.ReadBrew.controller;

import com.example.ReadBrew.dto.BookLibraryDTO;
import com.example.ReadBrew.model.Book;
import com.example.ReadBrew.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicCatalogController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/library")
    public ResponseEntity<Page<BookLibraryDTO>> getPublicLibrary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BookLibraryDTO> booksPage = bookRepository.findCompleteBooks(pageable)
                .map(this::convertToDto);

        return ResponseEntity.ok(booksPage);
    }

    private BookLibraryDTO convertToDto(Book book) {

        String category = (book.getCategories() != null && !book.getCategories().isEmpty())
                ? book.getCategories().get(0)
                : "Geral";

        return new BookLibraryDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getThumbnailUrl(),
                category,
                book.getDescription()
        );
    }
}