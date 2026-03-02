package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.ReadBrew.dto.ReadingCompletionResponseDTO;
import com.example.ReadBrew.dto.ReadingDiaryResponseDTO; 
import com.example.ReadBrew.dto.UserStatsDTO;
import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.CompleteReadingDTO;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.service.ReadingDiaryService;

@RestController
@RequestMapping("/api/v1/my-room")
public class ReadingDiaryController {

    @Autowired
    private ReadingDiaryService readingDiaryService;

    private User getUserLoggedIn() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @GetMapping("/my-books")
    public ResponseEntity<List<ReadingDiaryResponseDTO>> getMyBooks() {
        User loggedInUser = getUserLoggedIn(); 
        return ResponseEntity.ok(readingDiaryService.getUserEntries(loggedInUser.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<ReadingDiaryResponseDTO> addToProfile(@RequestBody BookResponseDTO bookDto) {
        User loggedInUser = getUserLoggedIn(); 
        ReadingDiaryResponseDTO response = readingDiaryService.addToProfile(loggedInUser.getId(), bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{diaryId}/start")
    public ResponseEntity<ReadingDiaryResponseDTO> startReading(@PathVariable Long diaryId) {
        User loggedInUser = getUserLoggedIn();
        ReadingDiaryResponseDTO updatedEntry = readingDiaryService.startReading(diaryId, loggedInUser.getId());
        return ResponseEntity.ok(updatedEntry);
    }

    @PatchMapping("/{diaryId}/complete")
    public ResponseEntity<ReadingCompletionResponseDTO> completeReading( 
            @PathVariable Long diaryId,
            @RequestBody CompleteReadingDTO dto) { 
        
        User loggedInUser = getUserLoggedIn();
        ReadingCompletionResponseDTO response = readingDiaryService.completeReading(diaryId, loggedInUser.getId(), dto);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> removeOrAbandon(@PathVariable Long diaryId) {
        User loggedInUser = getUserLoggedIn();
        readingDiaryService.removeOrAbandonBook(diaryId, loggedInUser.getId());
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getMyStats() {
        User loggedInUser = getUserLoggedIn();
        UserStatsDTO stats = readingDiaryService.getUserStats(loggedInUser.getId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping 
    public ResponseEntity<List<ReadingDiaryResponseDTO>> getMyRoom() {
        User loggedInUser = getUserLoggedIn(); 
        List<ReadingDiaryResponseDTO> diaryEntries = readingDiaryService.getUserEntries(loggedInUser.getId());
        return ResponseEntity.ok(diaryEntries);
    }
}