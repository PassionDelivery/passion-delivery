package com.example.pdelivery.cart.error;

import com.example.pdelivery.shared.error.PDeliveryException;

public class CartException extends PDeliveryException {

	public CartException(CartErrorCode errorCode) {
		super(errorCode);
	}
}
