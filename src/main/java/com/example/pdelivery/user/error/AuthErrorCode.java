package com.example.pdelivery.user.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

	// 회원가입 관련 (001~009)
	INVALID_ROLE("AUTH_001", HttpStatus.BAD_REQUEST, "MANAGER 또는 MASTER 역할로는 가입할 수 없습니다"),
	DUPLICATE_USERNAME("AUTH_002", HttpStatus.CONFLICT, "이미 존재하는 username입니다"),
	DUPLICATE_NICKNAME("AUTH_003", HttpStatus.CONFLICT, "이미 존재하는 nickname입니다"),
	DUPLICATE_EMAIL("AUTH_004", HttpStatus.CONFLICT, "이미 존재하는 email입니다"),

	// 로그인 관련 (100~199)
	INVALID_CREDENTIALS("AUTH_100", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
	ACCOUNT_DEACTIVATED("AUTH_101", HttpStatus.FORBIDDEN, "탈퇴한 계정입니다"),

	// 토큰/인가 관련 (200~209) — 현재 JwtAuthenticationEntryPoint, JwtAccessDeniedHandler에서 사용 예정
	TOKEN_EXPIRED("AUTH_200", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
	TOKEN_INVALID("AUTH_201", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
	TOKEN_MISSING("AUTH_202", HttpStatus.UNAUTHORIZED, "인증 토큰이 없습니다"),
	ACCESS_DENIED("AUTH_203", HttpStatus.FORBIDDEN, "접근 권한이 없습니다");

	private final String code;
	private final HttpStatus status;
	private final String message;

	AuthErrorCode(String code, HttpStatus status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public HttpStatus status() {
		return status;
	}

	@Override
	public String message() {
		return message;
	}

}
