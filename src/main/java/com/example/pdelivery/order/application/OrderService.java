package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.shared.security.AuthUser;

public interface OrderService {
	Order createOrder(UUID customerId, OrderCreateRequest req);

	void completeOrderPayment(UUID orderId);

	Slice<Order> getOrderItemsByCustomer(UUID customerId, Pageable pageable);

	Slice<Order> getOrderItemsByStore(UUID ownerId, UUID storeId, Pageable pageable);

	Order getOrder(AuthUser authUser, UUID orderId);

	void cancelOrder(UUID userId, UUID orderId, OrderCancelRequest req);

	void changeStatusOrder(UUID orderId, OrderChangeStatusRequest req);

	void deleteOrder(UUID customerId, UUID orderId);
}
