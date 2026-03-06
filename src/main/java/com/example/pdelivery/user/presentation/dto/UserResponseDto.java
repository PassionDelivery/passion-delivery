package com.example.pdelivery.user.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponseDto(
	UUID userId,
	String username,
	String nickname,
	String email,
	UserRole role,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt
) {
	public static UserResponseDto from(UserEntity user) {
		return new UserResponseDto(
			user.getId(),
			user.getUsername(),
			user.getNickname(),
			user.getEmail(),
			user.getRole(),
			user.getCreatedAt()
		);
	}
}
