package com.example.pdelivery.menu.infrastructure.required.store;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MenuStoreRequirerImpl implements MenuStoreRequirer {

	// private final StoreProvider storeProvider;
	private final StoreData storeData = new StoreData(UUID.randomUUID(), UUID.randomUUID());

	@Override
	public StoreData getStore(UUID storeId) {
		// StoreData storeData = storeProvider.getStore(storeId);
		if (storeData == null) {
			throw new MenuException(MenuErrorCode.STORE_NOT_FOUND);
		}
		return storeData;
		/*
			TODO: Store 모듈 구현 후 StoreProvider에 위임하도록 교체
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new MenuException(MenuErrorCode.PROVIDER_ERROR);
		 */
	}
}
