package com.example.pdelivery.user.application.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.AuthErrorCode;
import com.example.pdelivery.user.error.AuthException;
import com.example.pdelivery.user.presentation.dto.SignupRequestDto;
import com.example.pdelivery.user.presentation.dto.SignupResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SignupResponseDto signup(SignupRequestDto dto) {
		if (dto.getRole() == UserRole.MANAGER || dto.getRole() == UserRole.MASTER) {
			throw new AuthException(AuthErrorCode.INVALID_ROLE);
		}

		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new AuthException(AuthErrorCode.DUPLICATE_USERNAME);
		}
		if (userRepository.existsByNickname(dto.getNickname())) {
			throw new AuthException(AuthErrorCode.DUPLICATE_NICKNAME);
		}
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL);
		}
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		UserEntity user = UserEntity.create(dto.getUsername(), encodedPassword, dto.getNickname(), dto.getEmail(),
			dto.getRole());
		try {
			return SignupResponseDto.from(userRepository.save(user));
		} catch (DataIntegrityViolationException e) {
			String msg = e.getMostSpecificCause().getMessage();
			if (msg != null && msg.toLowerCase().contains("nickname")) {
				throw new AuthException(AuthErrorCode.DUPLICATE_NICKNAME, e);
			}
			if (msg != null && msg.toLowerCase().contains("email")) {
				throw new AuthException(AuthErrorCode.DUPLICATE_EMAIL, e);
			}
			throw new AuthException(AuthErrorCode.DUPLICATE_USERNAME, e);
		}
	}
}
