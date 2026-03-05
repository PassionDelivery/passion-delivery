package com.example.pdelivery.order.application;

import com.example.pdelivery.order.domain.Order;

public interface OrderService {
	Order createOrder(OrderRequest.OrderCreateRequest req);
}
