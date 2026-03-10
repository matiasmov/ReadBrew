package com.example.ReadBrew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReadBrew.dto.FeedResponseDTO;
import com.example.ReadBrew.model.User; 
import com.example.ReadBrew.service.TimelineService;

@RestController
@RequestMapping("/api/v1/feed")
public class TimelineController {

    @Autowired
    private TimelineService timelineService;

    // Friend's feed
    @PreAuthorize("isAuthenticated()") 
    @GetMapping
    public ResponseEntity<Page<FeedResponseDTO>> getFriendsFeed(
            @AuthenticationPrincipal User userLogado, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        int safeSize = Math.min(size, 50);
        int safePage = Math.max(page, 0);
        
        Page<FeedResponseDTO> feed = timelineService.getFollowersFeed(userLogado.getId(), PageRequest.of(safePage, safeSize));
        return ResponseEntity.ok(feed);
    }

    // timeline 
    @PreAuthorize("isAuthenticated()") 
    @GetMapping("/global")
    public ResponseEntity<Page<FeedResponseDTO>> getGlobalFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        int safeSize = Math.min(size, 50);
        int safePage = Math.max(page, 0); 
        
        Page<FeedResponseDTO> feed = timelineService.getGlobalFeed(PageRequest.of(safePage, safeSize));
        return ResponseEntity.ok(feed);
    }
}