package com.example.pdelivery.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.shared.security.JwtUtil;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.AuthErrorCode;
import com.example.pdelivery.user.error.AuthException;
import com.example.pdelivery.user.presentation.dto.LoginRequestDto;
import com.example.pdelivery.user.presentation.dto.LoginResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public LoginResponseDto login(LoginRequestDto dto) {
		UserEntity user = userRepository.findByUsernameAndDeletedAtIsNull(dto.getUsername())
			.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
		return new LoginResponseDto(token);
	}
}
