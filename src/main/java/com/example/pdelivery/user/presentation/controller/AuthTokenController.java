package com.example.pdelivery.user.presentation.controller;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.user.application.service.LoginService;
import com.example.pdelivery.user.presentation.dto.LoginRequestDto;
import com.example.pdelivery.user.presentation.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
public class AuthTokenController {

	private final LoginService loginService;

	@Operation(security = {})
	@PostMapping("/auth/tokens")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(
		@Valid @RequestBody LoginRequestDto dto) {
		return ApiResponse.ok(loginService.login(dto));
	}
}
