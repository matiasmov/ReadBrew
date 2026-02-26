package com.example.ReadBrew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ReadBrew.model.Coffee;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
  
}