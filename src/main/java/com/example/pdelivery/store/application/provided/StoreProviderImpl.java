package com.example.pdelivery.store.application.provided;

import java.util.List;
import java.util.UUID;

import com.example.pdelivery.shared.Provider;
import com.example.pdelivery.store.domain.StoreEntity;
import com.example.pdelivery.store.domain.StoreRepository;

import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
public class StoreProviderImpl implements StoreProvider {

	private final StoreRepository storeRepository;

	@Override
	public StoreInfo getStore(UUID storeId) {
		return storeRepository.findById(storeId)
			.map(entity -> new StoreInfo(entity.getId(), entity.getOwnerId()))
			.orElse(null);
	}

	@Override
	public boolean existsById(UUID storeId) {
		return storeRepository.existsById(storeId);
	}

	@Override
	public List<UUID> findStoreIdsByOwnerId(UUID ownerId) {
		return storeRepository.findStoreIdsByOwnerId(ownerId);
	}
}
