package com.example.ReadBrew.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ReadBrew.dto.FeedResponseDTO;
import com.example.ReadBrew.model.TimelineEvent;
import com.example.ReadBrew.model.User;
import com.example.ReadBrew.repository.TimelineEventRepository;
import com.example.ReadBrew.repository.UserRepository;

@Service
public class TimelineService {

    @Autowired
    private TimelineEventRepository timelineRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Page<FeedResponseDTO> getFollowersFeed(Long currentUserId, Pageable pageable) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Set<User> following = user.getFollowing();
        following.add(user); 

        return timelineRepository.findByUserInOrderByCreatedAtDesc(following, pageable)
                .map(this::convertToDTO);
    }

    public Page<FeedResponseDTO> getGlobalFeed(Pageable pageable) {
        return timelineRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    private FeedResponseDTO convertToDTO(TimelineEvent event) {
        return FeedResponseDTO.builder()
                .eventId(event.getId())
                .eventType(event.getEventType().name())
                .createdAt(event.getCreatedAt())
                .username(event.getUser().getNickname())
                .userAvatarUrl(event.getUser().getAvatarUrl())
                .bookTitle(event.getBook() != null ? event.getBook().getTitle() : null)
                .bookAuthor(event.getBook() != null && !event.getBook().getAuthors().isEmpty() ? event.getBook().getAuthors().get(0) : null)
                .bookCoverUrl(event.getBook() != null ? event.getBook().getThumbnailUrl() : null)
                .coffeeName(event.getCoffee() != null ? event.getCoffee().getName() : null)
                .coffeeIconUrl(event.getCoffee() != null ? event.getCoffee().getPixelArtUrl() : null)
                .rating(event.getRating())
                .reviewText(event.getReviewText())
                .achievementTitle(event.getAchievement() != null ? event.getAchievement().getTitle() : null)
                .achievementIconUrl(event.getAchievement() != null ? event.getAchievement().getImageUrl() : null)
                .build();
    }
}