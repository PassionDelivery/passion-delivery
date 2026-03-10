package com.example.pdelivery.order.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum OrderErrorCode implements ErrorCode {

	// 생성 시 확인 유효성
	INVALID_QUANTITY("ORDER_001", HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다."),
	INVALID_PRICE("ORDER_002", HttpStatus.BAD_REQUEST, "가격은 0원 이상이어야 합니다."),
	//cart, address, payment 에서의 오류
	ADDRESS_INVALID("ORDER_003", HttpStatus.BAD_REQUEST, "주소가 올바르지 않습니다."),
	CART_EMPTY("ORDER_004", HttpStatus.BAD_REQUEST, "장바구니가 비었습니다."),
	PAYMENT_FAILED("ORDER_005", HttpStatus.BAD_REQUEST, "결제 실패했습니다."),

	REQUIRED_PARAMETER_MISSING("ORDER_006", HttpStatus.BAD_REQUEST, "request가 NULL입니다."),
	INVALID_REASON("ORDER_007", HttpStatus.BAD_REQUEST, "사유를 입력해주세요."),
	INVALID_STATUS("ORDER_008", HttpStatus.BAD_REQUEST, "status가 올바르지 않습니다."),
	INVALID_OWNER("ORDER_009", HttpStatus.BAD_REQUEST, "가게 주인 ID가 올바르지 않습니다."),
	INVALID_CUSTOMER("ORDER_010", HttpStatus.BAD_REQUEST, "고객ID가 올바르지 않습니다."),

	// 조회
	ORDER_NOT_FOUND("ORDER_101", HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
	ORDER_MENU_NOT_FOUND("ORDER_102", HttpStatus.NOT_FOUND, "주문한 메뉴를 찾을 수 없습니다."),
	CART_NOT_FOUND("ORDER_103", HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),

	CANCEL_TIMEOUT("ORDER_201", HttpStatus.FORBIDDEN, "주문한지 5분이 지나 취소할 수 없습니다."),

	ALREADY_CANCELED("ORDER_301", HttpStatus.CONFLICT, "이미 취소된 주문입니다."),
	INVALID_CANCEL_STATUS("ORDER_302", HttpStatus.CONFLICT, "취소 불가능한 상태입니다."),
	ALREADY_ORDER_COMPLETED("ORDER_303", HttpStatus.CONFLICT, "이미 주문 완료(COMPLETED) 상태입니다."),
	INVALID_CHANGE_STATUS("ORDER_304", HttpStatus.CONFLICT, "주문 상태를 변경할 수 없습니다."),

	// TO DO: provider 오류
	PROVIDER_ERROR("ORDER_501", HttpStatus.BAD_GATEWAY, "외부 서비스 호출 중 오류가 발생했습니다."),
	PROVIDER_TIMEOUT("ORDER_502", HttpStatus.GATEWAY_TIMEOUT, "시스템 간 통신 중 오류가 발생했습니다.");

	/* TO DO:
	// STORE_CLOSED -> 가게 영업 시간 고려 시 추가할 부분
	// 품절/가격 고려 -> menu와도 통신 필요
	 */
	private final String code;
	private final HttpStatus status;
	private final String message;

	OrderErrorCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.status = httpStatus;
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
