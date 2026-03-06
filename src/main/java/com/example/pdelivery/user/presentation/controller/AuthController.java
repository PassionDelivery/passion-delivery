package com.example.pdelivery.user.presentation.controller;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.user.application.service.AuthService;
import com.example.pdelivery.user.presentation.dto.SignupRequestDto;
import com.example.pdelivery.user.presentation.dto.SignupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/users")
	public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto dto) {
		return ApiResponse.create(authService.signup(dto));
	}
}
