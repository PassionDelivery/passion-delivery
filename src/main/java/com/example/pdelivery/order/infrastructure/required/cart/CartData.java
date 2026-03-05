package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.List;
import java.util.UUID;

public record CartData(
	UUID storeId,
	List<UUID> menuIds
) {

}


