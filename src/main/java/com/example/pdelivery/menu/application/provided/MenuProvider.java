package com.example.pdelivery.menu.application.provided;

import java.util.Optional;
import java.util.UUID;

public interface MenuProvider {

	Optional<MenuInfo> getMenu(UUID menuId);
}
