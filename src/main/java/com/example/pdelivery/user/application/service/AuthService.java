package com.example.pdelivery.user.application.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
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
			throw new IllegalArgumentException("MANAGER 또는 MASTER 역할로는 가입할 수 없습니다.");
		}
		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 username입니다.");
		}
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		UserEntity user = UserEntity.create(dto.getUsername(), encodedPassword, dto.getNickname(), dto.getEmail(), dto.getRole());
		return SignupResponseDto.from(userRepository.save(user));
	}
}
