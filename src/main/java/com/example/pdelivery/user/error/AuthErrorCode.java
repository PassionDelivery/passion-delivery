package com.example.pdelivery.user.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

	INVALID_ROLE("AUTH_001", HttpStatus.BAD_REQUEST, "MANAGER 또는 MASTER 역할로는 가입할 수 없습니다"),
	DUPLICATE_USERNAME("AUTH_002", HttpStatus.CONFLICT, "이미 존재하는 username입니다");

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
