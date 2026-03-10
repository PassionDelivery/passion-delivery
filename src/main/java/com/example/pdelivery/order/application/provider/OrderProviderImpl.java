package com.example.pdelivery.order.application.provider;

import java.util.Optional;
import java.util.UUID;

import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.shared.annotations.Provider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Provider
public class OrderProviderImpl implements OrderProvider {
	private final OrderRepository orderRepository;

	@Override
	public Optional<OrderInfo> getOrderInfo(UUID orderId) {
		return orderRepository.findById(orderId).map(order -> order.toOrderInfo());
	}

}
