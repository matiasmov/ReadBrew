package com.example.ReadBrew.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ReadBrew.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> { 
    

    User findByEmail(String email); 
    List<User> findByNicknameContainingIgnoreCase(String nickname);
}