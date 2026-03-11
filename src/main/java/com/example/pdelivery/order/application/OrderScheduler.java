package com.example.pdelivery.order.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.domain.OrderStatusHistory;
import com.example.pdelivery.order.domain.OrderStatusHistoryRepository;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderScheduler {

	private final OrderRepository orderRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Scheduled(fixedRate = 60_000)
	@Transactional
	public void cancelExpiredUnpaidOrders() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
		List<Order> expiredOrders = orderRepository.findAllByStatusAndCreatedAtBefore(OrderStatus.UNPAID, cutoff);

		for (Order order : expiredOrders) {
			OrderStatus previous = order.getStatus();
			order.updateStatus(OrderStatus.CANCELLED);
			order.updateReason("결제 미완료로 자동 취소");
			orderStatusHistoryRepository.save(
				OrderStatusHistory.create(order.getId(), previous, OrderStatus.CANCELLED)
			);
		}

		if (!expiredOrders.isEmpty()) {
			log.info("UNPAID 자동 취소: {}건 처리", expiredOrders.size());
		}
	}
}
