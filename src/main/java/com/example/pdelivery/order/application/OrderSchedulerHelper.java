package com.example.pdelivery.order.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

	private static final int BATCH_SIZE = 100;

	private final OrderRepository orderRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Transactional
	public int cancelExpiredBatch(LocalDateTime cutoff) {
		Slice<Order> slice = orderRepository.findAllByStatusAndCreatedAtBefore(
			OrderStatus.UNPAID, cutoff, PageRequest.of(0, BATCH_SIZE));

		int count = 0;
		for (Order order : slice.getContent()) {
			if (order.getStatus() != OrderStatus.UNPAID) {
				continue;
			}
			OrderStatus previous = order.getStatus();
			order.updateStatus(OrderStatus.CANCELLED);
			order.updateReason("결제 미완료로 자동 취소");
			orderStatusHistoryRepository.save(
				OrderStatusHistory.create(order.getId(), previous, OrderStatus.CANCELLED)
			);
			count++;
		}
		return count;
	}

	public boolean hasMore(LocalDateTime cutoff) {
		Slice<Order> slice = orderRepository.findAllByStatusAndCreatedAtBefore(
			OrderStatus.UNPAID, cutoff, PageRequest.of(0, 1));
		return !slice.isEmpty();
	}
}
