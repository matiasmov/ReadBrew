package com.example.ReadBrew.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.BookResponseDTO;
import com.example.ReadBrew.model.ReadingDiary;
import com.example.ReadBrew.model.ReadingStatus; 
import com.example.ReadBrew.service.ReadingDiaryService;



@RestController
@RequestMapping("/api/my-room")
public class ReadingDiaryController {

    @Autowired
    private ReadingDiaryService readingDiaryService;

  
    @PostMapping("/add")
    public ResponseEntity<ReadingDiary> addBook(
            @RequestParam Long userId,
            @RequestParam ReadingStatus status,
            @RequestBody BookResponseDTO bookDto) {
        
        ReadingDiary savedEntry = readingDiaryService.addBookToRoom(userId, bookDto, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReadingDiary>> getUserRoom(@PathVariable Long userId) {
        List<ReadingDiary> diaryEntries = readingDiaryService.getUserEntries(userId);
        return ResponseEntity.ok(diaryEntries);
    }
}