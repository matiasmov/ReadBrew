package com.example.ReadBrew.dto;

import com.example.ReadBrew.model.Avatar;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    int level,
    int xp,
    Avatar profileAvatar
) {}