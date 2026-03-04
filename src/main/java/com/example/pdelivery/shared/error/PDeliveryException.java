package com.example.pdelivery.shared.error;

import lombok.Getter;

@Getter
public class PDeliveryException extends RuntimeException {

	private final ErrorCode errorCode;

	public PDeliveryException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	public PDeliveryException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public PDeliveryException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.message(), cause);
		this.errorCode = errorCode;
	}

	public PDeliveryException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
}
