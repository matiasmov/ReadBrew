package com.example.ReadBrew.repository;

import com.example.ReadBrew.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> { 
    

    User findByEmail(String email); 
}