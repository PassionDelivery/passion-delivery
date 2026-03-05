package com.example.pdelivery.order.domain;

import java.util.UUID;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

public record OrderLineVO(
	UUID menuId,
	String menuName,
	Integer quantity,
	Integer price
) {
	public OrderLineVO {
		if (quantity < 1) {
			throw new OrderException(OrderErrorCode.INVALID_QUANTITY);
		}
		if (price < 0) {
			throw new OrderException(OrderErrorCode.INVALID_PRICE);
		}
	}
}
