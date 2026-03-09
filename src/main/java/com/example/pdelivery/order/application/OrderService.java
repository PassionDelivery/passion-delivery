package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.shared.PageResponse;

public interface OrderService {
	Order createOrder(UUID customerId, OrderCreateRequest req);

	PageResponse getOrderItemsByCustomer(UUID customerId, Pageable pageable);

	PageResponse getOrderItemsByStore(UUID storeId, Pageable pageable);

	Order getOrder(UUID orderId);

	void cancelOrder(UUID orderId, OrderCancelRequest req);

	void changeStatusOrder(UUID orderId, OrderChangeStatusRequest req);

	void deleteOrder(UUID orderId);
}
