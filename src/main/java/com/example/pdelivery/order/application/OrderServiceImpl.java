package com.example.pdelivery.order.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.infrastructure.required.address.OrderAddressRequirer;
import com.example.pdelivery.order.infrastructure.required.cart.CartData;
import com.example.pdelivery.order.infrastructure.required.cart.OrderCartRequirer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final OrderCartRequirer orderCartRequirer;
	private final OrderAddressRequirer orderAddressRequirer;

	//추후 삭제 예정
	private UUID customerId = UUID.randomUUID();

	@Transactional
	@Override
	public Order createOrder(OrderRequest.OrderCreateRequest req) {
		String address = orderAddressRequirer.getAddress(req.deliveryAddressId());
		List<CartData> cartLines = orderCartRequirer.getCartLines(req.cartId());

		List<OrderLineVO> orderLineVOs = cartLines.stream()
			.map(cartData -> new OrderLineVO(
				cartData.menuId(),
				cartData.menuName(),
				cartData.quantity(),
				cartData.price()
			)).toList();

		Order order = Order.create(req.storeId(), address, customerId, orderLineVOs);

		orderRepository.save(order);

		return order;
	}
}
