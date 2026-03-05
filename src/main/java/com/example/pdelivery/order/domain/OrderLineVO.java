package com.example.pdelivery.order.domain;

import java.util.UUID;

public record OrderLineVO(
	UUID menuId,
	String menuName,
	Integer quantity,
	Integer price
) {
	public OrderLineVO {
		if (quantity < 1) {
			throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
		}
		if (price < 0) {
			throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
		}
	}
}
