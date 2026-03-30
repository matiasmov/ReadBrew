package com.example.ReadBrew.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.example.ReadBrew.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByExternalId(String externalId);

    @Query("SELECT b FROM Book b WHERE b.title IS NOT NULL AND b.title <> '' " +
            "AND b.thumbnailUrl IS NOT NULL AND b.thumbnailUrl <> '' " +
            "AND b.authors IS NOT EMPTY")
    Page<Book> findCompleteBooks(Pageable pageable);

}