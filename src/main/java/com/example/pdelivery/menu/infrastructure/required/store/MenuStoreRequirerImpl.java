package com.example.pdelivery.menu.infrastructure.required.store;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MenuStoreRequirerImpl implements MenuStoreRequirer {

	@Override
	public boolean existsById(UUID storeId) {
		// TODO: Store 모듈 구현 후 StoreMenuProvider에 위임하도록 교체
		return true;
	}
}
