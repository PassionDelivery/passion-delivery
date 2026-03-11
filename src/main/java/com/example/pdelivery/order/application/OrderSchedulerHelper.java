package com.example.pdelivery.order.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.domain.OrderStatusHistory;
import com.example.pdelivery.order.domain.OrderStatusHistoryRepository;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderSchedulerHelper {

	private final OrderRepository orderRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Transactional
	public int cancelBatch(List<Order> orders) {
		int count = 0;
		for (Order detached : orders) {
			UUID orderId = detached.getId();
			Order order = orderRepository.findById(orderId).orElse(null);
			if (order == null || order.getStatus() != OrderStatus.UNPAID) {
				continue;
			}
			OrderStatus previous = order.getStatus();
			order.updateStatus(OrderStatus.CANCELLED);
			order.updateReason("결제 미완료로 자동 취소");
			orderStatusHistoryRepository.save(
				OrderStatusHistory.create(orderId, previous, OrderStatus.CANCELLED)
			);
			count++;
		}
		return count;
	}
}
