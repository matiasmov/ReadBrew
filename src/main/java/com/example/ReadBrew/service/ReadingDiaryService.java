package com.example.ReadBrew.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.CompleteReadingDTO; 
import com.example.ReadBrew.model.Book;
import com.example.ReadBrew.model.Coffee;
import com.example.ReadBrew.model.ReadingDiary;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.model.enums.ReadingStatus;
import com.example.ReadBrew.repository.BookRepository;
import com.example.ReadBrew.repository.CoffeeRepository;
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

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Transactional
    public ReadingDiary addToProfile(Long userId, BookResponseDTO googleBook) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Book book = bookRepository.findByExternalId(googleBook.getId())
                .orElseGet(() -> {
                    Book newBook = new Book();
                    newBook.setExternalId(googleBook.getId());
                    newBook.setTitle(googleBook.getTitle());
                    newBook.setAuthors(googleBook.getAuthors());
                    newBook.setCategories(googleBook.getCategories());
                    newBook.setThumbnailUrl(googleBook.getThumbnailUrl());
                    return bookRepository.save(newBook);
                });

        if (readingDiaryRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("This book is already in your profile!");
        }

        ReadingDiary entry = new ReadingDiary();
        entry.setUser(user);
        entry.setBook(book);
        entry.setStatus(ReadingStatus.TO_READ);
        entry.setRecommendedCoffee(null); 
        entry.setCoffeeConsumed(false);

        return readingDiaryRepository.save(entry);
    }


    @Transactional
    public ReadingDiary startReading(Long diaryId, Long userId) {
        ReadingDiary diary = readingDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary entry not found."));

        if (diary.getUser().getId() != userId) {
            throw new RuntimeException("Access Denied: Not your book!");
        }

        if (diary.getStatus() != ReadingStatus.TO_READ) {
            throw new RuntimeException("You have already started or finished this reading.");
        }

        Coffee suggestion = getCoffeeRecommendation(diary.getBook().getCategories());
        
        diary.setRecommendedCoffee(suggestion);
        diary.setStatus(ReadingStatus.READING);
        
        return readingDiaryRepository.save(diary);
    }


    @Transactional
    public ReadingDiary completeReading(Long diaryId, Long userId, CompleteReadingDTO dto) {
        ReadingDiary diary = readingDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary entry not found."));

      if (diary.getUser().getId() != userId) {
            throw new RuntimeException("Access Denied!");
        }

        if (diary.getStatus() == ReadingStatus.READ) {
            throw new RuntimeException("Already completed.");
        }

        Coffee coffee = coffeeRepository.findById(dto.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found."));

        
        diary.setCoffee(coffee);
        diary.setCoffeeConsumed(true);
        diary.setLikedCoffee(dto.isLikedCoffee());
        
        
        diary.setBookRating(dto.getBookRating());
        diary.setReview(dto.getReview());

        diary.setStatus(ReadingStatus.READ);
        diary.setFinishedAt(LocalDateTime.now());

     
        User user = diary.getUser();
        user.setXp(user.getXp() + 50);
        if (user.getXp() >= 100) {
            user.setLevel(user.getLevel() + 1);
            user.setXp(user.getXp() - 100);
        }

        userRepository.save(user);
        return readingDiaryRepository.save(diary);
    }

    public List<ReadingDiary> getUserEntries(Long userId) {
        return readingDiaryRepository.findByUserId(userId);
    }

  
    private Coffee getCoffeeRecommendation(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return coffeeRepository.findByNameContainingIgnoreCase("Latte").orElse(null);
        }

        String mainCat = categories.get(0).toLowerCase();

        if (mainCat.contains("fiction") || mainCat.contains("fantasy")) {
            return coffeeRepository.findByNameContainingIgnoreCase("Expresso").orElse(null);
        } else if (mainCat.contains("romance")) {
            return coffeeRepository.findByNameContainingIgnoreCase("Cappuccino").orElse(null);
        } else if (mainCat.contains("horror") || mainCat.contains("mystery")) {
            return coffeeRepository.findByNameContainingIgnoreCase("Preto").orElse(null);
        }

        return coffeeRepository.findById(1L).orElse(null);
    }
}