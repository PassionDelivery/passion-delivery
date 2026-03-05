package com.example.pdelivery.menu.application.provided;

import java.util.UUID;

public interface MenuOrderProvider {

	String getMenuName(UUID menuId);

	Integer getMenuPrice(UUID menuId);
}
