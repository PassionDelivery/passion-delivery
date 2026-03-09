package com.example.pdelivery.cart.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum CartErrorCode implements ErrorCode {

	MENU_NOT_FOUND("CART_001", HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
	CART_ITEM_NOT_FOUND("CART_002", HttpStatus.NOT_FOUND, "장바구니 항목을 찾을 수 없습니다."),
	CART_NOT_FOUND("CART_003", HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),
	INVALID_QUANTITY("CART_004", HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	CartErrorCode(String code, HttpStatus status, String message) {
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
