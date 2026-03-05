package com.example.pdelivery.order.infrastructure.required.address;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.address.application.provided.AddressOrderProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderAddressRequirerImpl implements OrderAddressRequirer {
	private final AddressOrderProvider addressOrderProvider;

	@Override
	public String getAddress(UUID deliveryAddressId) {
		return addressOrderProvider.getAddress(deliveryAddressId);
	}
}
