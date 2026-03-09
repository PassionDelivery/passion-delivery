package com.example.pdelivery.menu.infrastructure.required.store;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;
import com.example.pdelivery.store.application.provided.StoreProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MenuStoreRequirerImpl implements MenuStoreRequirer {

	private final StoreProvider storeProvider;

	@Override
	public StoreData getStore(UUID storeId) {
		com.example.pdelivery.store.application.provided.StoreInfo data = storeProvider.getStore(storeId);
		if (data == null) {
			throw new MenuException(MenuErrorCode.STORE_NOT_FOUND);
		}
		return new StoreData(data.storeId(), data.ownerId());
	}
}
