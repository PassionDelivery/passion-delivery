package com.example.pdelivery.user.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class AuthException extends PDeliveryException {

	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}

	public AuthException(AuthErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public AuthException(AuthErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public AuthException(AuthErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

}
