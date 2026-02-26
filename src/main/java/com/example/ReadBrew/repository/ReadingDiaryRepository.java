package com.example.ReadBrew.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ReadBrew.model.ReadingDiary;

@Repository
public interface ReadingDiaryRepository extends JpaRepository<ReadingDiary, Long> {
    List<ReadingDiary> findByUserId(Long userId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}