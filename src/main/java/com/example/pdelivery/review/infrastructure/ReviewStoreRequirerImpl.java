package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import com.example.pdelivery.shared.Requirer;
import com.example.pdelivery.store.application.provided.StoreProvider;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class ReviewStoreRequirerImpl implements ReviewStoreRequirer {

	private final StoreProvider storeProvider;

	@Override
	public boolean existsBy(UUID storeId) {
		return storeProvider.existsById(storeId);
	}
}
