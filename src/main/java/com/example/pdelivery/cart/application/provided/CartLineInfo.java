package com.example.pdelivery.cart.application.provided;

import java.util.UUID;

public record CartLineInfo(
	Integer no,
	UUID menuId,
	Integer quantity
) {
}
