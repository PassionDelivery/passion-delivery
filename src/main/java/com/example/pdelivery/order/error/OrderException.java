package com.example.pdelivery.order.error;

import com.example.pdelivery.shared.error.ErrorCode;
import com.example.pdelivery.shared.error.PDeliveryException;

public class OrderException extends PDeliveryException {
	public OrderException(ErrorCode errorCode) {
		super(errorCode);
	}

	public OrderException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public OrderException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public OrderException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
