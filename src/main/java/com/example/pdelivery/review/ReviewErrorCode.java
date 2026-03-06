package com.example.pdelivery.review;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum ReviewErrorCode implements ErrorCode {
	REVIEW_RATING_BOUNDED_ERROR("REVIEW_001", HttpStatus.BAD_REQUEST, "평점이 올바르지 않습니다."), REVIEW_CONTENT_SIZE_ERROR(
		"REVIEW_002", HttpStatus.BAD_REQUEST, "리뷰 내용은 공백이 아니고, 200자 이내여야 합니다."),

	REVIEW_VALIDATOR_STORE_ID("REVIEW_101", HttpStatus.BAD_REQUEST, "가게 아이디는 필수값 입니다."), REVIEW_VALIDATOR_ORDER_ID(
		"REVIEW_102", HttpStatus.BAD_REQUEST, "주문 아이디는 필수값 입니다."), REVIEW_VALIDATOR_RATING("REVIEW_103",
		HttpStatus.BAD_REQUEST, "평점은 필수값 입니다."), REVIEW_VALIDATOR_CONTENT("REVIEW_104", HttpStatus.BAD_REQUEST,
		"리뷰내용은 필수값 입니다."),

	REVIEW_USER_NOT_FOUND("REVIEW_201", HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."), REVIEW_STORE_NOT_FOUND("REVIEW_202",
		HttpStatus.BAD_REQUEST, "가게가 존재하지 않습니다."), REVIEW_ORDER_NOT_FOUND("REVIEW_203", HttpStatus.BAD_REQUEST,
		"주문이 존재하지 않습니다."), REVIEW_ORDER_INVALID_STATUS("REVIEW_204", HttpStatus.BAD_REQUEST, "리뷰를 달 수 없는 주문입니다.");
	private final String code;
	private final HttpStatus status;
	private final String message;

	ReviewErrorCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.status = httpStatus;
		this.message = message;
	}

	@Override
	public String code() {
		return this.code;
	}

	@Override
	public HttpStatus status() {
		return this.status;
	}

	@Override
	public String message() {
		return this.message;
	}
}
