package com.example.pdelivery.order.application;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

	private static final int BATCH_SIZE = 100;

	private final OrderRepository orderRepository;
	private final OrderStatusHistoryRepository orderStatusHistoryRepository;

	@Scheduled(fixedRate = 60_000)
	@Transactional
	public void cancelExpiredUnpaidOrders() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
		int totalCancelled = 0;

		Slice<Order> slice;
		do {
			slice = orderRepository.findAllByStatusAndCreatedAtBefore(
				OrderStatus.UNPAID, cutoff, PageRequest.of(0, BATCH_SIZE));

			for (Order order : slice.getContent()) {
				OrderStatus previous = order.getStatus();
				order.updateStatus(OrderStatus.CANCELLED);
				order.updateReason("결제 미완료로 자동 취소");
				orderStatusHistoryRepository.save(
					OrderStatusHistory.create(order.getId(), previous, OrderStatus.CANCELLED)
				);
			}
			totalCancelled += slice.getNumberOfElements();
		} while (slice.hasNext());

		if (totalCancelled > 0) {
			log.info("UNPAID 자동 취소: {}건 처리", totalCancelled);
		}
	}
}
