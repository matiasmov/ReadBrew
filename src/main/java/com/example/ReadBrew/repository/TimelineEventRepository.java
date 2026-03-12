package com.example.ReadBrew.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ReadBrew.model.TimelineEvent;
import com.example.ReadBrew.model.User;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    
    // LEFT JOIN
    @EntityGraph(attributePaths = {"user", "user.profileAvatar", "book", "coffee", "achievement"})
    Page<TimelineEvent> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @EntityGraph(attributePaths = {"user", "user.profileAvatar", "book", "coffee", "achievement"})
    Page<TimelineEvent> findByUserInOrderByCreatedAtDesc(Collection<User> users, Pageable pageable);
    boolean existsByUserIdAndCreatedAtAfter(Long userId, LocalDateTime timestamp);
}