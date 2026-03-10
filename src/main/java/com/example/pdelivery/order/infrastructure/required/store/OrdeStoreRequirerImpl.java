package com.example.pdelivery.order.infrastructure.required.store;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.store.application.provided.StoreProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrdeStoreRequirerImpl implements OrderStoreRequirer {
	private final StoreProvider storeProvider;

	@Override
	public String getStoreName(UUID storeId) {
		/*
		TO DO: store존재 체크 필요
		 */
		String storeName = storeProvider.getStore(storeId).storeName();
		return storeName;
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}

	public UUID getOwnerId(UUID storeId) {
		return storeProvider.getStore(storeId).ownerId();
	}
}
