package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.dto.CompleteReadingDTO;
import com.example.ReadBrew.model.ReadingDiary;
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


    @PostMapping("/add")
    public ResponseEntity<ReadingDiary> addToProfile(@RequestBody BookResponseDTO bookDto) {
        User loggedInUser = getUserLoggedIn(); 
        ReadingDiary savedEntry = readingDiaryService.addToProfile(loggedInUser.getId(), bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
    }

   
    @PatchMapping("/{diaryId}/start")
    public ResponseEntity<ReadingDiary> startReading(@PathVariable Long diaryId) {
        User loggedInUser = getUserLoggedIn();
        ReadingDiary updatedEntry = readingDiaryService.startReading(diaryId, loggedInUser.getId());
        return ResponseEntity.ok(updatedEntry);
    }

  
    @PatchMapping("/{diaryId}/complete")
    public ResponseEntity<ReadingDiary> completeReading(
            @PathVariable Long diaryId,
            @RequestBody CompleteReadingDTO dto) { 
        
        User loggedInUser = getUserLoggedIn();
        ReadingDiary updatedEntry = readingDiaryService.completeReading(diaryId, loggedInUser.getId(), dto);
        return ResponseEntity.ok(updatedEntry);
    }

  
    @GetMapping 
    public ResponseEntity<List<ReadingDiary>> getMyRoom() {
        User loggedInUser = getUserLoggedIn(); 
        List<ReadingDiary> diaryEntries = readingDiaryService.getUserEntries(loggedInUser.getId());
        return ResponseEntity.ok(diaryEntries);
    }
}