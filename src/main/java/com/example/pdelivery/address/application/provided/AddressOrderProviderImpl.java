package com.example.pdelivery.address.application.provided;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class AddressOrderProviderImpl implements AddressOrderProvider {
	@Override
	public String getAddress(UUID addressId) {
		return "";
	}
}
