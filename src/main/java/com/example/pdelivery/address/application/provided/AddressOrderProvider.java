package com.example.pdelivery.address.application.provided;

import java.util.UUID;

public interface AddressOrderProvider {
	String getAddress(UUID addressId);
}
