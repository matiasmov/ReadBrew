package com.example.ReadBrew.repository;

import com.example.ReadBrew.model.Reading;
import com.example.ReadBrew.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findByUser(User user);

    boolean existsByUserAndBookId(User user, Long bookId);
}