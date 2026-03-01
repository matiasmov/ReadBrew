package com.example.ReadBrew.repository;

import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ReadBrew.model.Coffee;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
   
    Optional<Coffee> findByNameContainingIgnoreCase(String name);
}