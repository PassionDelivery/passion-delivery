package com.example.pdelivery.user.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.user.application.service.UserService;
import com.example.pdelivery.user.presentation.dto.UpdateUserRequestDto;
import com.example.pdelivery.user.presentation.dto.UserResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/{username}")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
		@PathVariable String username,
		Authentication authentication
	) {
		String currentUsername = authentication.getName();
		return ApiResponse.ok(userService.getUser(username, currentUsername));
	}

	@PatchMapping("/{username}")
	public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
		@PathVariable String username,
		@Valid @RequestBody UpdateUserRequestDto dto,
		Authentication authentication
	) {
		String currentUsername = authentication.getName();
		return ApiResponse.ok(userService.updateUser(username, currentUsername, dto));
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(
		@PathVariable String username,
		Authentication authentication
	) {
		String currentUsername = authentication.getName();
		userService.deleteUser(username, currentUsername);
		return ResponseEntity.noContent().build();
	}

}
