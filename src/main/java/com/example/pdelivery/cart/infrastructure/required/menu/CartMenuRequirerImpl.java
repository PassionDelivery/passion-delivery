package com.example.pdelivery.cart.infrastructure.required.menu;

import java.util.UUID;

import com.example.pdelivery.cart.error.CartErrorCode;
import com.example.pdelivery.cart.error.CartException;
import com.example.pdelivery.menu.application.provided.MenuInfo;
import com.example.pdelivery.menu.application.provided.MenuProvider;
import com.example.pdelivery.shared.Requirer;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class CartMenuRequirerImpl implements CartMenuRequirer {

	private final MenuProvider menuProvider;

	@Override
	public MenuData getMenu(UUID menuId) {
		MenuInfo menuInfo = menuProvider.getMenu(menuId)
			.orElseThrow(() -> new CartException(CartErrorCode.MENU_NOT_FOUND));

		return new MenuData(menuInfo.menuId(), menuInfo.storeId(), menuInfo.name(), menuInfo.price());
	}

	@Override
	public void validateMenuBelongsToStore(UUID menuId, UUID storeId) {
		MenuData menuData = getMenu(menuId);

		if (!menuData.storeId().equals(storeId)) {
			throw new CartException(CartErrorCode.MENU_STORE_MISMATCH);
		}
	}
}
