package com.example.pdelivery.payment.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class PaymentException extends PDeliveryException {

	public PaymentException(PaymentErrorCode errorCode) {
		super(errorCode);
	}

	public PaymentException(PaymentErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public PaymentException(PaymentErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public PaymentException(PaymentErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
