package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;
import static com.example.pdelivery.order.error.OrderErrorCode.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.order.infrastructure.required.address.OrderAddressRequirer;
import com.example.pdelivery.order.infrastructure.required.cart.CartData;
import com.example.pdelivery.order.infrastructure.required.cart.OrderCartRequirer;
import com.example.pdelivery.order.infrastructure.required.menu.MenuData;
import com.example.pdelivery.order.infrastructure.required.menu.OrderMenuRequirer;
import com.example.pdelivery.order.infrastructure.required.payment.OrderPaymentRequirer;
import com.example.pdelivery.shared.enums.OrderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final OrderCartRequirer orderCartRequirer;
	private final OrderAddressRequirer orderAddressRequirer;
	private final OrderPaymentRequirer orderPaymentRequirer;
	private final OrderMenuRequirer orderMenuRequirer;

	//추후 삭제 예정
	private UUID customerId = UUID.randomUUID();

	@Transactional
	@Override
	public Order createOrder(OrderCreateRequest req) {
		String address = orderAddressRequirer.getAddress(req.deliveryAddressId());
		CartData cartLines = orderCartRequirer.getCartLines(req.cartId());
		List<UUID> menuIds = cartLines.cartItems().stream().map(CartData.CartItems::menuId).toList();
		List<MenuData> menuData = orderMenuRequirer.getMenus(menuIds);

		Map<UUID, MenuData> menuMap = new HashMap<>();
		for (MenuData menu : menuData) {
			menuMap.put(menu.menuId(), menu);
		}

		List<OrderLineVO> orderLineVOs = cartLines.cartItems().stream().map(cartItem -> {
			MenuData menu = menuMap.get(cartItem.menuId());
			if (menu == null) {
				throw new OrderException(OrderErrorCode.ORDER_MENU_NOT_FOUND);
			}
			return new OrderLineVO(cartItem.menuId(), menu.menuName(), cartItem.quantity(), menu.price());
		}).toList();

		UUID storeId = cartLines.storeId();
		Order order = Order.create(storeId, address, customerId, orderLineVOs);

		if (orderPaymentRequirer.processPayment(order.getId(), order.getTotalPrice())) {
			orderRepository.save(order);
		}

		return order;
	}

	@Transactional
	@Override
	public void cancelOrder(UUID orderId, OrderCancelRequest req) {
		//TO DO: OrderId의 Order customerId랑 userId랑 같은지 확인

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		if (order.checkCancellatioin())
			throw new OrderException(OrderErrorCode.ALREADY_CANCELED);
		//5분 이내만 취소 가능
		if (checkCancelTimeout(order.getCreatedAt()))
			throw new OrderException(CANCEL_TIMEOUT);

		//TO DO: 결제 취소 요청

		order.updateStatus(OrderStatus.CANCELLED);
		order.updateReason(req.reason());
	}

	private boolean checkCancelTimeout(LocalDateTime orderCreatedAt) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime timeoutLimit = orderCreatedAt.plusMinutes(5);

		return now.isAfter(timeoutLimit);
	}
}
