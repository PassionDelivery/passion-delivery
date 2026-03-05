package com.example.pdelivery.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.UserErrorCode;
import com.example.pdelivery.user.error.UserException;
import com.example.pdelivery.user.presentation.dto.UpdateUserRequestDto;
import com.example.pdelivery.user.presentation.dto.UserResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserResponseDto getUser(String username, String currentUsername) {
		if (!username.equals(currentUsername)) {
			throw new UserException(UserErrorCode.USER_FORBIDDEN);
		}
		UserEntity user = userRepository.findByUsernameAndDeletedAtIsNull(username)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		return UserResponseDto.from(user);
	}

	@Transactional
	public UserResponseDto updateUser(String username, String currentUsername, UpdateUserRequestDto dto) {
		if (!username.equals(currentUsername)) {
			throw new UserException(UserErrorCode.USER_FORBIDDEN);
		}
		if (dto.getNickname() == null && dto.getEmail() == null && dto.getPassword() == null) {
			throw new UserException(UserErrorCode.USER_FORBIDDEN);
		}
		UserEntity user = userRepository.findByUsernameAndDeletedAtIsNull(username)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		if (dto.getNickname() != null
			&& userRepository.existsByNicknameAndUsernameNot(dto.getNickname(), username)) {
			throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
		}
		if (dto.getEmail() != null
			&& userRepository.existsByEmailAndUsernameNot(dto.getEmail(), username)) {
			throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
		}
		String encodedPassword = dto.getPassword() != null
			? passwordEncoder.encode(dto.getPassword())
			: null;
		user.update(dto.getNickname(), dto.getEmail(), encodedPassword);
		return UserResponseDto.from(user);
	}

	@Transactional
	public void deleteUser(String username, String currentUsername) {
		if (!username.equals(currentUsername)) {
			throw new UserException(UserErrorCode.USER_FORBIDDEN);
		}
		UserEntity user = userRepository.findByUsernameAndDeletedAtIsNull(username)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		user.softDelete(user.getId());
	}

}
