package com.example.pdelivery.order.infrastructure.required.address;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderAddressRequirerImpl implements OrderAddressRequirer {
	// private final AddressProvider addressProvider;
	private String address = "address";

	@Override
	public String getAddress(UUID deliveryAddressId) {
		// String address = addressProvider.getAddress(deliveryAddressId);
		if (address == null || address.isBlank()) {
			throw new OrderException(OrderErrorCode.ADDRESS_INVALID);
		}
		return address;
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
