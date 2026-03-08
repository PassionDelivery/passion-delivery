package com.example.pdelivery.payment.error;

import org.springframework.http.HttpStatus;

import com.example.pdelivery.shared.error.ErrorCode;

public enum PaymentErrorCode implements ErrorCode {

	// 생성/요청 유효성
	INVALID_AMOUNT("PAYMENT_001", HttpStatus.BAD_REQUEST, "결제 금액이 올바르지 않습니다."),
	INVALID_ORDER_ID("PAYMENT_002", HttpStatus.BAD_REQUEST, "주문 ID가 유효하지 않습니다."),
	INVALID_STORE_ID("PAYMENT_003", HttpStatus.BAD_REQUEST, "가게 ID가 유효하지 않습니다."),
	UNSUPPORTED_METHOD("PAYMENT_004", HttpStatus.BAD_REQUEST, "지원하지 않는 결제 수단입니다."),
	INVALID_STATUS_TRANSITION("PAYMENT_005", HttpStatus.CONFLICT, "결제 상태를 변경할 수 없습니다."),
	INVALID_PAYMENT_METHOD("PAYMENT_006", HttpStatus.BAD_REQUEST, "결제 수단이 유효하지 않습니다."),
	INVALID_PAYMENT_PROVIDER("PAYMENT_007", HttpStatus.BAD_REQUEST, "결제사가 유효하지 않습니다."),
	UNAUTHORIZED_ORDER_ACCESS("PAYMENT_008", HttpStatus.BAD_REQUEST, "본인의 주문만 결제할 수 있습니다."),
	INVALID_CUSTOMER("PAYMENT_009", HttpStatus.FORBIDDEN, "고객 ID가 유효하지 않습니다."),

	// 조회/상태
	PAYMENT_NOT_FOUND("PAYMENT_101", HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
	ALREADY_PAID("PAYMENT_102", HttpStatus.CONFLICT, "이미 완료된 결제입니다."),

	// 외부 오류
	PROVIDER_ERROR("PAYMENT_201", HttpStatus.BAD_GATEWAY, "결제사 처리 중 오류가 발생했습니다."),
	PROVIDER_TIMEOUT("PAYMENT_202", HttpStatus.GATEWAY_TIMEOUT, "결제사 응답이 지연되고 있습니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	PaymentErrorCode(String code, HttpStatus httpStatus, String message) {
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
