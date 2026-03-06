package com.example.pdelivery.order.application.provider;

import java.util.List;
import java.util.UUID;

import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.shared.enums.OrderStatus;

public record OrderInfo(UUID orderId,
						UUID storeId,
						UUID customerId,
						String address,
						OrderStatus status,
						String reason,
						Integer totalPrice,
						List<OrderLineVO> orderLines) {
}
