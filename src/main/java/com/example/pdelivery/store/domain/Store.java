package com.example.pdelivery.store.domain;

import com.example.pdelivery.store.error.StoreErrorCode;
import com.example.pdelivery.store.error.StoreException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "address", nullable = false, length = 100)
	private String address;

	@Column(name = "phone", length = 20)
	private String phone;

	public Store(String name, String address, String phone) {
		if (name == null || name.isBlank() || name.length() > 50) {
			throw new StoreException(StoreErrorCode.INVALID_STORE_NAME);
		}
		if (address == null || address.isBlank() || address.length() > 100) {
			throw new StoreException(StoreErrorCode.INVALID_STORE_ADDRESS);
		}
		if (phone != null && phone.length() > 20) {
			throw new StoreException(StoreErrorCode.INVALID_STORE_PHONE);
		}

		this.name = name;
		this.address = address;
		this.phone = phone;
	}
}
