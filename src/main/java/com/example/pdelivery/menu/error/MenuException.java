package com.example.pdelivery.menu.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class MenuException extends PDeliveryException {

	public MenuException(MenuErrorCode errorCode) {
		super(errorCode);
	}

	public MenuException(MenuErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public MenuException(MenuErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public MenuException(MenuErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
