package com.example.pdelivery.review;

import com.example.pdelivery.shared.error.PDeliveryException;

public class ReviewException extends PDeliveryException {
	public ReviewException(ReviewErrorCode errorCode) {
		super(errorCode);
	}

	public ReviewException(ReviewErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public ReviewException(ReviewErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public ReviewException(ReviewErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
