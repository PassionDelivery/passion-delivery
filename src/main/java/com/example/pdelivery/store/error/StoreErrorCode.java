package com.example.pdelivery.store.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum StoreErrorCode implements ErrorCode {

	// 유효성
	INVALID_STORE_NAME("STORE_001", HttpStatus.BAD_REQUEST, "가게 이름이 올바르지 않습니다."),
	INVALID_STORE_ADDRESS("STORE_002", HttpStatus.BAD_REQUEST, "가게 주소가 올바르지 않습니다."),
	INVALID_STORE_PHONE("STORE_003", HttpStatus.BAD_REQUEST, "가게 전화번호가 올바르지 않습니다."),

	// 조회
	STORE_NOT_FOUND("STORE_101", HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
	CATEGORY_NOT_FOUND("STORE_102", HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

	// 상태
	INVALID_STATUS_CHANGE("STORE_201", HttpStatus.CONFLICT, "상태를 변경할 수 없습니다."),

	// 권한
	NOT_STORE_OWNER("STORE_301", HttpStatus.FORBIDDEN, "본인 소유의 가게만 수정/삭제할 수 있습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	StoreErrorCode(String code, HttpStatus status, String message) {
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
