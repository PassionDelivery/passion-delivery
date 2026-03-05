package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import com.example.pdelivery.shared.Requirer;

@Requirer
public class ReviewStoreRequirerImpl implements ReviewStoreRequirer {
	@Override
	public boolean existsBy(UUID storeId) {
		return true;
	}
}
