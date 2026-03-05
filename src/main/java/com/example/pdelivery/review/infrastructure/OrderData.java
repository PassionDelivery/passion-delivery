package com.example.pdelivery.review.infrastructure;

import com.example.pdelivery.order.domain.OrderStatus;

public record OrderData(
	OrderStatus orderStatus
) {
}
