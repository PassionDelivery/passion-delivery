package com.example.pdelivery.order.infrastructure.required.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderMenuRequirerImpl implements OrderMenuRequirer {
	// private final MenuProvider menuProvider;
	// menuProvider로 가져오는 로직으로 대체될 예정
	private List<MenuData> menuData = new ArrayList<>();

	public List<MenuData> getMenus(List<UUID> MenuIds) {
		return menuData;

		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
