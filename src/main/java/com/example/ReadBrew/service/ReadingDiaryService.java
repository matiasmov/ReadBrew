package com.example.ReadBrew.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.CompleteReadingDTO; 
import com.example.ReadBrew.dto.ReadingCompletionResponseDTO;
import com.example.ReadBrew.dto.UserStatsDTO;
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
                    
                    newBook.setTitle(googleBook.getTitle() != null ? googleBook.getTitle() : "unknown title");
                    newBook.setAuthors(googleBook.getAuthors() != null ? googleBook.getAuthors() : List.of("unknown author"));
                    
                    if (googleBook.getCategories() == null || googleBook.getCategories().isEmpty()) {
                        newBook.setCategories(List.of("Fiction"));
                    } else {
                        newBook.setCategories(googleBook.getCategories());
                    }

                    String thumb = (googleBook.getThumbnailUrl() != null && !googleBook.getThumbnailUrl().isBlank()) 
                                   ? googleBook.getThumbnailUrl() 
                                   : "/images/books/default_cover.png";
                    newBook.setThumbnailUrl(thumb);
                    
                    String desc = (googleBook.getDescription() != null && !googleBook.getDescription().isBlank())
                                   ? googleBook.getDescription()
                                   : "The synopsis for this book is not available.";
                    newBook.setDescription(desc);
                    
                    newBook.setPageCount(googleBook.getPageCount());

                    return bookRepository.save(newBook);
                });

        if (readingDiaryRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("This book is already on your shelf");
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
    public ReadingCompletionResponseDTO completeReading(Long diaryId, Long userId, CompleteReadingDTO dto) {
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
        int xpGained = 50;
        user.setXp(user.getXp() + xpGained);
        
        boolean leveledUp = false; 
        if (user.getXp() >= 100) {
            user.setLevel(user.getLevel() + 1);
            user.setXp(user.getXp() - 100);
            leveledUp = true;
        }

        userRepository.save(user);
        readingDiaryRepository.save(diary);

        return ReadingCompletionResponseDTO.builder()
                .id(diary.getId())
                .status(diary.getStatus().name())
                .finishedAt(diary.getFinishedAt())
                .bookTitle(diary.getBook().getTitle())
                .bookAuthor(diary.getBook().getAuthors() != null && !diary.getBook().getAuthors().isEmpty() ? diary.getBook().getAuthors().get(0) : "Unknown author")
                .xpGained(xpGained)
                .currentXp(user.getXp())
                .currentLevel(user.getLevel())
                .levelUp(leveledUp)
                .coffeeName(coffee.getName())
                .likedCoffee(diary.getLikedCoffee())
                .bookRating(diary.getBookRating())
                .review(diary.getReview())
                .build();
    }
    public UserStatsDTO getUserStats(Long userId) {
      
        List<ReadingDiary> completedBooks = readingDiaryRepository.findByUserIdAndStatus(userId, ReadingStatus.READ);

        
        if (completedBooks == null || completedBooks.isEmpty()) {
            return new UserStatsDTO(0L, 0, "He hasn't tasted any coffee yet.", Collections.emptyList());
        }

        
        long totalPages = completedBooks.stream()
                .map(diary -> diary.getBook().getPageCount())
                .filter(Objects::nonNull) 
                .mapToLong(Integer::longValue)
                .sum();

       
        String favoriteCoffee = completedBooks.stream()
                .filter(ReadingDiary::getLikedCoffee) 
                .map(diary -> diary.getCoffee() != null ? diary.getCoffee().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(name -> name, Collectors.counting())) 
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("don't have a favorite yet.");

    
        List<String> topGenres = completedBooks.stream()
                .map(diary -> diary.getBook().getCategories())
                .filter(Objects::nonNull) 
                .flatMap(List::stream) 
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting())) 
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3) 
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new UserStatsDTO(totalPages, completedBooks.size(), favoriteCoffee, topGenres);
    }

    public List<ReadingDiary> getUserEntries(Long userId) {
        return readingDiaryRepository.findByUserId(userId);
    }

    private Coffee getCoffeeRecommendation(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return coffeeRepository.findById(1L).orElse(null);
        }

        List<String> lowerCaseCategories = categories.stream()
                .map(String::toLowerCase)
                .toList();

        return coffeeRepository.findBestMatchByCategories(lowerCaseCategories)
                .orElseGet(() -> coffeeRepository.findById(1L).orElse(null));
    }
}