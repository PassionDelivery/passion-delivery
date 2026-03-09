package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;
import static com.example.pdelivery.order.error.OrderErrorCode.*;
import static com.example.pdelivery.order.presentation.OrderResponse.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.order.infrastructure.required.cart.CartData;
import com.example.pdelivery.order.infrastructure.required.cart.OrderCartRequirer;
import com.example.pdelivery.order.infrastructure.required.menu.MenuData;
import com.example.pdelivery.order.infrastructure.required.menu.OrderMenuRequirer;
import com.example.pdelivery.order.infrastructure.required.payment.OrderPaymentRequirer;
import com.example.pdelivery.order.infrastructure.required.store.OrderStoreRequirer;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final OrderCartRequirer orderCartRequirer;
	// private final OrderAddressRequirer orderAddressRequirer;
	private final OrderPaymentRequirer orderPaymentRequirer;
	private final OrderMenuRequirer orderMenuRequirer;
	private final OrderStoreRequirer orderStoreRequirer;

	//추후 삭제 예정
	private UUID customerId = UUID.randomUUID();

	@Transactional
	@Override
	public Order createOrder(UUID customerId, OrderCreateRequest req) {
		// String address = orderAddressRequirer.getAddress(req.deliveryAddressId());
		String address = req.address();
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

	@Transactional(readOnly = true)
	@Override
	public PageResponse getOrderItemsByCustomer(UUID customerId, Pageable pageable) {
		//TO DO: customer 존재 확인

		Slice<Order> orderItems = orderRepository.findAllByCustomerId(customerId, pageable);

		List<OrderDataResponse> orderListData = orderItems.getContent().stream()
			.map(order -> {
				OrderDataResponse summaryResponse = order.toSummaryResponse();
				UUID storeId = order.getStoreId();
				String storeName = orderStoreRequirer.getStoreName((storeId));

				summaryResponse.updateStoreInfo(storeId, storeName);
				return summaryResponse;
			})
			.toList();

		PageResponse data = new PageResponse(
			orderListData,
			// orderItems.getNumber(),
			// orderItems.getSize(),
			orderItems.hasNext()
		);

		return data;
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse getOrderItemsByStore(UUID storeId, Pageable pageable) {
		//TO DO: store 존재 확인

		Slice<Order> orderItems = orderRepository.findAllByStoreId(storeId, pageable);

		List<OrderDataResponse> orderListData = orderItems.getContent().stream()
			.map(order -> {
				OrderDataResponse summaryResponse = order.toSummaryResponse();
				return summaryResponse;
			})
			.toList();

		PageResponse data = new PageResponse(
			orderListData,
			orderItems.hasNext()
		);

		return data;
	}

	@Transactional(readOnly = true)
	@Override
	public Order getOrder(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

		return order;
	}

	@Transactional
	@Override
	public void cancelOrder(UUID orderId, OrderCancelRequest req) {
		//TO DO: OrderId의 Order customerId랑 userId랑 같은지 확인

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		//중복 취소
		if (order.checkCancellation()) {
			throw new OrderException(OrderErrorCode.ALREADY_CANCELED);
		}
		//PENDING 상태에서만 취소 가능
		if (!order.checkPending()) {
			throw new OrderException(INVALID_CANCEL_STATUS);
		}
		//5분 이내만 취소 가능
		if (checkCancelTimeout(order.getCreatedAt())) {
			throw new OrderException(CANCEL_TIMEOUT);
		}

		//TO DO: 결제 취소 요청

		order.updateStatus(OrderStatus.CANCELLED);
		order.updateReason(req.reason());
	}

	private boolean checkCancelTimeout(LocalDateTime orderCreatedAt) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime timeoutLimit = orderCreatedAt.plusMinutes(5);

		return now.isAfter(timeoutLimit);
	}

	@Transactional
	@Override
	public void changeStatusOrder(UUID orderId, OrderChangeStatusRequest req) {
		//owner, manager인지 체크 필요
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		if (order.checkCancellation()) {
			throw new OrderException(OrderErrorCode.ALREADY_CANCELED);
		}

		//완료 시 더 이상 상태 변경 불가능
		if (order.checkCompleted()) {
			throw new OrderException(OrderErrorCode.ALREADY_ORDER_COMPLETED);
		}

		if (req.orderStatus().equals(OrderStatus.REJECTED)) {
			if (req.reason() == null || req.reason().isBlank()) {
				throw new OrderException(OrderErrorCode.INVALID_REASON, "거절 사유를 입력해주세요.");
			}
			//reject는 PENDING 상태에서만 가능
			if (!order.checkPending()) {
				throw new OrderException(OrderErrorCode.INVALID_CHANGE_STATUS, "reject는 PENDING 상태에서만 가능합니다.");
			}
			//TO DO: 결제 취소 요청
			order.updateReason(req.reason());
		}

		order.updateStatus(req.orderStatus());
	}

	//TO DO: 환불 요청 추가시 관련 로직 필요

	@Transactional
	@Override
	public void deleteOrder(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		order.softDelete(customerId); //수정: customerId

	}
}