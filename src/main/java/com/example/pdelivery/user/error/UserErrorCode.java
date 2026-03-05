package com.example.pdelivery.user.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	USER_FORBIDDEN("USER_002", HttpStatus.FORBIDDEN, "본인의 정보만 접근할 수 있습니다."),
	NO_UPDATE_FIELD("USER_003", HttpStatus.BAD_REQUEST, "수정할 필드를 최소 1개 이상 입력해야 합니다."),
	DUPLICATE_NICKNAME("USER_004", HttpStatus.CONFLICT, "이미 존재하는 nickname입니다."),
	DUPLICATE_EMAIL("USER_005", HttpStatus.CONFLICT, "이미 존재하는 email입니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	UserErrorCode(String code, HttpStatus status, String message) {
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
