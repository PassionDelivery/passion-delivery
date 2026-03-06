package com.example.pdelivery.menu.application.provided;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuProvider {

	Optional<MenuInfo> getMenu(UUID menuId);

	List<MenuInfo> getMenus(List<UUID> menuIds);
}
