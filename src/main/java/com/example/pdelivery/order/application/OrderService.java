package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;

public interface OrderService {
	Order createOrder(UUID customerId, OrderCreateRequest req);

	void completeOrderPayment(UUID orderId);

	PageResponse getOrderItemsByCustomer(UUID customerId, Pageable pageable);

	PageResponse getOrderItemsByStore(UUID ownerId, UUID storeId, Pageable pageable);

	Order getOrder(AuthUser authUser, UUID orderId);

	void cancelOrder(UUID userId, UUID orderId, OrderCancelRequest req);

	void changeStatusOrder(UUID orderId, OrderChangeStatusRequest req);

	void deleteOrder(UUID customerId, UUID orderId);
}
