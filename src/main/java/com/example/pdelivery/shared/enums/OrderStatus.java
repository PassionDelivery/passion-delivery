package com.example.pdelivery.shared.enums;

import java.util.Arrays;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

public enum OrderStatus {
	PENDING, ACCEPTED, COOKED, REJECTED, COMPLETED, CANCELLED;

	public static OrderStatus compareString(String status) {
		return Arrays.stream(OrderStatus.values())
			.findFirst()
			.orElseThrow(() -> new OrderException(OrderErrorCode.INVALID_STATUS, "존재하지 않는 status입니다."));

	}
}
