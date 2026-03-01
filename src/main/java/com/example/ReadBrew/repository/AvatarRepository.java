package com.example.ReadBrew.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ReadBrew.model.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    
}