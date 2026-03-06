package com.example.pdelivery.shared.security;

import java.util.UUID;

public record AuthUser(UUID userId, String username) {
}
