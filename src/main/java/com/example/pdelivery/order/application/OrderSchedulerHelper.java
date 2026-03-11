package com.example.pdelivery.order.application;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderStatusHistory;
import com.example.pdelivery.order.domain.OrderStatusHistoryRepository;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderSchedulerHelper {

	private final OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Transactional
	public int cancelBatch(List<Order> orders) {
		for (Order order : orders) {
			OrderStatus previous = order.getStatus();
			order.updateStatus(OrderStatus.CANCELLED);
			order.updateReason("결제 미완료로 자동 취소");
			orderStatusHistoryRepository.save(
				OrderStatusHistory.create(order.getId(), previous, OrderStatus.CANCELLED)
			);
		}
		return orders.size();
	}
}
