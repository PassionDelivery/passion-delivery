package com.example.pdelivery.order.infrastructure.required.menu;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.application.provided.MenuProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderMenuRequirerImpl implements OrderMenuRequirer {
	private final MenuProvider menuProvider;
	// menuProvider로 가져오는 로직으로 대체될 예정
	// private List<MenuData> menuData = new ArrayList<>();

	public List<MenuData> getMenus(List<UUID> menuIds) {
		List<MenuData> menuData = menuProvider.getMenus(menuIds).stream()
			.map(menuInfo ->
				new MenuData(menuInfo.menuId(), menuInfo.name(), menuInfo.price())
			)
			.toList();
		// menuData.add(new MenuData(UUID.randomUUID(), "후라이드 치킨", 17000));
		// menuData.add(new MenuData(UUID.randomUUID(), "양념 치킨", 18000));
		return menuData;

		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
