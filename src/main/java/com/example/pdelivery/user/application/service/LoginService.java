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

	// TODO: [보안] 타이밍 공격을 통한 사용자 열거(User Enumeration) 취약점
	//   현재 사용자가 존재하지 않을 경우 즉시 AuthException을 던지고,
	//   사용자가 존재할 때만 passwordEncoder.matches()를 실행하므로
	//   응답 시간 차이로 username 존재 여부를 유추할 수 있다.
	//   수정 방향: 사용자가 없는 경우에도 동일한 비용의 더미 해시 검증을 수행한 뒤 예외를 던져야 한다.
	//   관련 식별자: passwordEncoder.matches, AuthException, AuthErrorCode.INVALID_CREDENTIALS
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
