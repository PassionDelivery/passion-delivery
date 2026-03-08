package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;

import java.util.UUID;

import com.example.pdelivery.order.domain.Order;

public interface OrderService {
	Order createOrder(OrderCreateRequest req);

	void cancelOrder(UUID orderId, OrderCancelRequest req);

	void changeStatusOrder(UUID orderId, OrderChangeStatusRequest req);
}
