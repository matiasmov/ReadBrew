package com.example.ReadBrew.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.model.Book;
import com.example.ReadBrew.model.ReadingDiary;
import com.example.ReadBrew.model.ReadingStatus;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.BookRepository;
import com.example.ReadBrew.repository.ReadingDiaryRepository;
import com.example.ReadBrew.repository.UserRepository; 

@Service
public class ReadingDiaryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReadingDiaryRepository readingDiaryRepository;

    @Transactional
    public ReadingDiary addBookToRoom(Long userId, BookResponseDTO googleBook, ReadingStatus status) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Book book = bookRepository.findByExternalId(googleBook.getId())
                .orElseGet(() -> {
                    Book newBook = new Book();
                    newBook.setExternalId(googleBook.getId());
                    newBook.setTitle(googleBook.getTitle());
                    newBook.setAuthors(googleBook.getAuthors());
                    newBook.setCategories(googleBook.getCategories());
                    newBook.setDescription(googleBook.getDescription());
                    
                    return bookRepository.save(newBook); 
                });

        if (readingDiaryRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("Book already exists in the user's room.");
        }

        ReadingDiary diaryEntry = new ReadingDiary();
        diaryEntry.setUser(user);
        diaryEntry.setBook(book);
        diaryEntry.setStatus(status);
        diaryEntry.setCoffeeConsumed(false); 

        return readingDiaryRepository.save(diaryEntry);
    }

   
    public List<ReadingDiary> getUserEntries(Long userId) {
    
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("\"User not found.");
        }
        return readingDiaryRepository.findByUserId(userId);
    }
}