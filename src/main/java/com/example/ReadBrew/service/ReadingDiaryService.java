package com.example.ReadBrew.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.CompleteReadingDTO; 
import com.example.ReadBrew.dto.ReadingCompletionResponseDTO;
import com.example.ReadBrew.dto.ReadingDiaryResponseDTO;
import com.example.ReadBrew.dto.UserStatsDTO;
import com.example.ReadBrew.model.Book;
import com.example.ReadBrew.model.Coffee;
import com.example.ReadBrew.model.ReadingDiary;
import com.example.ReadBrew.model.TimelineEvent;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.model.enums.ReadingStatus;
import com.example.ReadBrew.model.enums.TimelineEventType;
import com.example.ReadBrew.repository.BookRepository;
import com.example.ReadBrew.repository.CoffeeRepository;
import com.example.ReadBrew.repository.ReadingDiaryRepository;
import com.example.ReadBrew.repository.TimelineEventRepository;
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

    // Treating a Circular Dependency
    @Lazy
    @Autowired
    private AchievementService achievementService;

    @Autowired
    private TimelineEventRepository timelineEventRepository;

    @Transactional
    public ReadingDiaryResponseDTO addToProfile(Long userId, BookResponseDTO googleBook) {
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
                                   ? googleBook.getThumbnailUrl() : "/images/books/default_cover.png";
                    newBook.setThumbnailUrl(thumb);
                    
                    String desc = (googleBook.getDescription() != null && !googleBook.getDescription().isBlank())
                                   ? googleBook.getDescription() : "No synopsis available.";
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
        entry.setCoffeeConsumed(false);

        ReadingDiary savedEntry = readingDiaryRepository.save(entry);

        return ReadingDiaryResponseDTO.builder()
                .id(savedEntry.getId())
                .status(savedEntry.getStatus().name())
                .bookTitle(book.getTitle())
                .author(book.getAuthors().get(0))
                .thumbnailUrl(book.getThumbnailUrl())
                .userName(user.getNickname())
                .recommendedCoffee("Not recommended yet") 
                .build();
    }

    @Transactional
    public ReadingDiaryResponseDTO startReading(Long diaryId, Long userId) {
        ReadingDiary diary = readingDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary entry not found."));

        if (diary.getUser().getId() != userId) {
            throw new RuntimeException("Access Denied!");
        }

        if (diary.getStatus() != ReadingStatus.TO_READ) {
            throw new RuntimeException("You have already started or finished this reading.");
        }

        Coffee suggestion = getCoffeeRecommendation(diary.getBook().getCategories());
        
        diary.setRecommendedCoffee(suggestion);
        diary.setStatus(ReadingStatus.READING);
        
        ReadingDiary savedDiary = readingDiaryRepository.save(diary);


        TimelineEvent event = new TimelineEvent();
        event.setUser(diary.getUser());
        event.setEventType(TimelineEventType.STARTED_READING);
        event.setBook(diary.getBook());
        event.setCreatedAt(LocalDateTime.now());
        timelineEventRepository.save(event);

        return convertToDTO(savedDiary);
    }

    private ReadingDiaryResponseDTO convertToDTO(ReadingDiary diary) {
        Coffee coffee = diary.getRecommendedCoffee();
        
        return ReadingDiaryResponseDTO.builder()
                .id(diary.getId())
                .status(diary.getStatus().name())
                .bookTitle(diary.getBook().getTitle())
                .author(diary.getBook().getAuthors() != null && !diary.getBook().getAuthors().isEmpty() 
                        ? diary.getBook().getAuthors().get(0) : "Unknown")
                .thumbnailUrl(diary.getBook().getThumbnailUrl())
                .userName(diary.getUser().getNickname())
                .recommendedCoffee(coffee != null ? coffee.getName() : "None")
                .recommendedCoffeeDescription(coffee != null ? coffee.getDescription() : null)
                .recommendedCoffeeRecipe(coffee != null ? coffee.getRecipe() : null)
                .recommendedCoffeeImageUrl(coffee != null ? coffee.getPixelArtUrl() : null)
                .build();
    }

    @Transactional
public ReadingCompletionResponseDTO completeReading(Long diaryId, Long userId, CompleteReadingDTO dto) {
    ReadingDiary diary = readingDiaryRepository.findById(diaryId)
            .orElseThrow(() -> new RuntimeException("Diary entry not found."));

    if (diary.getUser().getId() != userId) {
        throw new RuntimeException("Acesso Negado!");
    }

    if (diary.getStatus() == ReadingStatus.READ) {
        throw new RuntimeException("Esta leitura já foi concluída.");
    }

    if (diary.getRecommendedCoffee() == null) {
        throw new RuntimeException("Nenhuma recomendação encontrada. Por favor, comece a leitura corretamente primeiro.");
    }

   // anti-spam
    LocalDateTime trintaSegundosAtras = LocalDateTime.now().minusSeconds(30);
    boolean postouRecentemente = timelineEventRepository.existsByUserIdAndCreatedAtAfter(userId, trintaSegundosAtras);

    if (postouRecentemente) {
        throw new RuntimeException("Calma, aventureiro! Você está lendo rápido demais. Aguarde 30 segundos para postar novamente.");
    }
        diary.setCoffee(diary.getRecommendedCoffee());
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

        TimelineEvent event = new TimelineEvent();
        event.setUser(user);
        event.setEventType(TimelineEventType.FINISHED_READING);
        event.setBook(diary.getBook());
        event.setCoffee(diary.getCoffee());
        event.setRating(diary.getBookRating());
        event.setReviewText(diary.getReview());
        event.setCreatedAt(LocalDateTime.now());
        timelineEventRepository.save(event);

        UserStatsDTO stats = getUserStats(user.getId());
        achievementService.checkAndUnlockAchievements(user, diary, stats.getBooksCompleted(), stats.getTopGenres());

        return ReadingCompletionResponseDTO.builder()
                .id(diary.getId())
                .status(diary.getStatus().name())
                .finishedAt(diary.getFinishedAt())
                .bookTitle(diary.getBook().getTitle())
                .bookAuthor(diary.getBook().getAuthors().get(0))
                .xpGained(xpGained)
                .currentXp(user.getXp())
                .currentLevel(user.getLevel())
                .levelUp(leveledUp)
                .coffeeName(diary.getCoffee().getName()) 
                .likedCoffee(diary.getLikedCoffee())
                .bookRating(diary.getBookRating())
                .review(diary.getReview())
                .build();
    }

    public List<ReadingDiaryResponseDTO> getUserEntries(Long userId) {
        List<ReadingDiary> entries = readingDiaryRepository.findByUserId(userId);

        return entries.stream()
                .map(diary -> ReadingDiaryResponseDTO.builder()
                        .id(diary.getId())
                        .status(diary.getStatus().name())
                        .bookTitle(diary.getBook().getTitle())
                        .author(diary.getBook().getAuthors() != null && !diary.getBook().getAuthors().isEmpty() 
                                ? diary.getBook().getAuthors().get(0) : "Unknown")
                        .thumbnailUrl(diary.getBook().getThumbnailUrl())
                        .userName(diary.getUser().getNickname())
                        .recommendedCoffee(diary.getRecommendedCoffee() != null ? 
                                           diary.getRecommendedCoffee().getName() : "None")
                        .build())
                .collect(Collectors.toList());
    }

    public UserStatsDTO getUserStats(Long userId) {
        List<ReadingDiary> completedBooks = readingDiaryRepository.findByUserIdAndStatus(userId, ReadingStatus.READ);
        
        if (completedBooks == null || completedBooks.isEmpty()) {
            return new UserStatsDTO(0L, 0, "No coffee tasted yet.", Collections.emptyList());
        }
        
        long totalPages = completedBooks.stream()
                .map(d -> d.getBook().getPageCount())
                .filter(Objects::nonNull) 
                .mapToLong(Integer::longValue)
                .sum();

        String favoriteCoffee = completedBooks.stream()
                .filter(ReadingDiary::getLikedCoffee) 
                .map(d -> d.getCoffee() != null ? d.getCoffee().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(name -> name, Collectors.counting())) 
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No favorite yet.");
    
        List<String> topGenres = completedBooks.stream()
                .map(d -> d.getBook().getCategories())
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

    @Transactional
    public void removeOrAbandonBook(Long diaryId, Long userId) {
        ReadingDiary diary = readingDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Book not found on the shelf."));

        if (diary.getUser().getId() != userId) {
            throw new RuntimeException("Access Denied!");
        }

        if (diary.getStatus() == ReadingStatus.TO_READ) {
            readingDiaryRepository.delete(diary);
        } 
      
        else if (diary.getStatus() == ReadingStatus.READING) {
            diary.setStatus(ReadingStatus.ABANDONED);
            readingDiaryRepository.save(diary);
            
            TimelineEvent event = new TimelineEvent();
            event.setUser(diary.getUser());
            event.setEventType(TimelineEventType.ABANDONED_BOOK);
            event.setBook(diary.getBook());
            event.setCreatedAt(LocalDateTime.now());
            timelineEventRepository.save(event);
        }
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