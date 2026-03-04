package com.example.pdelivery.payment.error;

import com.example.pdelivery.shared.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    // 생성/요청 유효성
    INVALID_AMOUNT("PAYMENT_001", HttpStatus.BAD_REQUEST, "결제 금액이 올바르지 않습니다."),
    UNSUPPORTED_METHOD("PAYMENT_002", HttpStatus.BAD_REQUEST, "지원하지 않는 결제 수단입니다."),
    INVALID_STATUS_TRANSITION("PAYMENT_003", HttpStatus.CONFLICT, "결제 상태를 변경할 수 없습니다."),

    // 조회/상태
    PAYMENT_NOT_FOUND("PAYMENT_101", HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    ALREADY_PAID("PAYMENT_102", HttpStatus.CONFLICT, "이미 완료된 결제입니다."),

    PROVIDER_ERROR("PAYMENT_201", HttpStatus.BAD_GATEWAY, "결제사 처리 중 오류가 발생했습니다."),
    PROVIDER_TIMEOUT("PAYMENT_202", HttpStatus.GATEWAY_TIMEOUT, "결제사 응답이 지연 되고 있습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.status = httpStatus;
        this.message = message;
    }

    @Override public String code() {
        return code;
    }
    @Override public HttpStatus status() {
        return status;
    }
    @Override public String message() {
        return message;
    }

}
