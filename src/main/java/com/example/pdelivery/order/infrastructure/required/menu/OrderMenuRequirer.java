package com.example.pdelivery.order.infrastructure.required.menu;

import java.util.List;
import java.util.UUID;

public interface OrderMenuRequirer {
	public List<MenuData> getMenus(List<UUID> MenuIds);
}
