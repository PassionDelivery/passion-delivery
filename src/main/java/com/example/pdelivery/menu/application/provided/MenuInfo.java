package com.example.pdelivery.menu.application.provided;

import java.util.UUID;

public record MenuInfo(
	UUID menuId,
	String name,
	Integer price,
	String description,
	Boolean isHidden
) {
}
