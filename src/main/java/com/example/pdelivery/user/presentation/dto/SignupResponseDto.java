package com.example.pdelivery.user.presentation.dto;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;

public record SignupResponseDto(
	String username,
	String nickname,
	String email,
	UserRole role
) {
	public static SignupResponseDto from(UserEntity user) {
		return new SignupResponseDto(
			user.getUsername(),
			user.getNickname(),
			user.getEmail(),
			user.getRole()
		);
	}
}
