package com.example.pdelivery.menu.application.provided;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.domain.MenuEntity;
import com.example.pdelivery.menu.domain.MenuRepository;
import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MenuOrderProviderImpl implements MenuOrderProvider {

	private final MenuRepository menuRepository;

	@Override
	public String getMenuName(UUID menuId) {
		return findMenuOrThrow(menuId).getMenu().getName();
	}

	@Override
	public Integer getMenuPrice(UUID menuId) {
		return findMenuOrThrow(menuId).getMenu().getPrice();
	}

	private MenuEntity findMenuOrThrow(UUID menuId) {
		return menuRepository.findById(menuId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));
	}
}
