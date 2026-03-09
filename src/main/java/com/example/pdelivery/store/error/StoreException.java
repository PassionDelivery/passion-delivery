package com.example.pdelivery.store.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class StoreException extends PDeliveryException {

	public StoreException(StoreErrorCode errorCode) {
		super(errorCode);
	}

	public StoreException(StoreErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public StoreException(StoreErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public StoreException(StoreErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
