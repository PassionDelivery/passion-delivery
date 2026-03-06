package com.example.pdelivery.order.application.provider;

import java.util.Optional;
import java.util.UUID;

public interface OrderProvider {
	Optional<OrderInfo> getOrderInfo(UUID orderId);
}
