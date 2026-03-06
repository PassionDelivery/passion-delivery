package com.example.pdelivery.user.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class UserException extends PDeliveryException {

	public UserException(UserErrorCode errorCode) {
		super(errorCode);
	}

	public UserException(UserErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
