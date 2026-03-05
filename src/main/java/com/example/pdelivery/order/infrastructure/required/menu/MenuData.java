package com.example.pdelivery.order.infrastructure.required.menu;

import java.util.UUID;

public record MenuData(
	UUID menuId,
	String menuName,
	Integer price,
	Integer quantity
) {

}


