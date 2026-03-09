package com.example.pdelivery.user.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Page<UserResponseDto> getUsers(String username, Pageable pageable) {
		return userRepository.findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(username, pageable)
			.map(UserResponseDto::from);
	}

	public UserResponseDto getUser(UUID userId) {
		UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		return UserResponseDto.from(user);
	}

	@Transactional
	public UserResponseDto updateUser(UUID userId, UpdateUserRequestDto dto) {
		if (dto.getNickname() == null && dto.getEmail() == null && dto.getPassword() == null) {
			throw new UserException(UserErrorCode.NO_UPDATE_FIELD);
		}
		UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		if (dto.getNickname() != null
			&& userRepository.existsByNicknameAndIdNotAndDeletedAtIsNull(dto.getNickname(), userId)) {
			throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
		}
		if (dto.getEmail() != null
			&& userRepository.existsByEmailAndIdNotAndDeletedAtIsNull(dto.getEmail(), userId)) {
			throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
		}
		String encodedPassword = dto.getPassword() != null
			? passwordEncoder.encode(dto.getPassword())
			: null;
		user.update(dto.getNickname(), dto.getEmail(), encodedPassword);
		return UserResponseDto.from(user);
	}

	@Transactional
	public void deleteUser(UUID userId, UUID deletedBy) {
		UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		user.softDelete(deletedBy);
	}

}
