package com.example.pdelivery.shared.security;

import java.util.UUID;

import com.example.pdelivery.user.domain.entity.UserRole;

public record AuthUser(UUID userId, String username, UserRole role) {
}
