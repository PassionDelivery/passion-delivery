package com.example.pdelivery.user.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.security.AuthUser;
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

	@GetMapping
	@PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
	public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUsers(
		@RequestParam(defaultValue = "") String username,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ApiResponse.ok(userService.getUsers(username, pageable));
	}

	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('MASTER') or authentication.principal.userId == #userId")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
		@PathVariable UUID userId
	) {
		return ApiResponse.ok(userService.getUser(userId));
	}

	@PatchMapping("/{userId}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('MASTER') or authentication.principal.userId == #userId")
	public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
		@PathVariable UUID userId,
		@Valid @RequestBody UpdateUserRequestDto dto
	) {
		return ApiResponse.ok(userService.updateUser(userId, dto));
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('MASTER') or authentication.principal.userId == #userId")
	public ResponseEntity<Void> deleteUser(
		@PathVariable UUID userId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		userService.deleteUser(userId, authUser.userId());
		return ResponseEntity.noContent().build();
	}

}
