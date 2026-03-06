package com.example.pdelivery.menu.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum MenuErrorCode implements ErrorCode {

	// 유효성
	INVALID_MENU_NAME("MENU_001", HttpStatus.BAD_REQUEST, "메뉴 이름이 올바르지 않습니다."),
	INVALID_MENU_PRICE("MENU_002", HttpStatus.BAD_REQUEST, "메뉴 가격이 올바르지 않습니다."),
	INVALID_MENU_DESCRIPTION("MENU_003", HttpStatus.BAD_REQUEST, "메뉴 설명이 올바르지 않습니다."),

	// 조회
	STORE_NOT_FOUND("MENU_004", HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
	MENU_NOT_FOUND("MENU_101", HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
	MENU_STORE_MISMATCH("MENU_103", HttpStatus.NOT_FOUND, "해당 가게의 메뉴가 아닙니다."),

	// 상태
	MENU_ALREADY_DELETED("MENU_201", HttpStatus.CONFLICT, "이미 삭제된 메뉴입니다."),

	// AI
	AI_GENERATION_FAILED("MENU_401", HttpStatus.BAD_GATEWAY, "AI 설명 생성에 실패했습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	MenuErrorCode(String code, HttpStatus status, String message) {
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
