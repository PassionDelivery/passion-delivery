package com.example.pdelivery.menu.application.provided;

import java.util.UUID;

public record MenuInfo(
	UUID menuId,
	UUID storeId,
	String name,
	Integer price,
	String description,
	Boolean isHidden
) {
}
