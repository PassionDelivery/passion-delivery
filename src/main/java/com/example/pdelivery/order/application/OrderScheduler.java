package com.example.pdelivery.order.application;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderScheduler {

	private static final int BATCH_SIZE = 100;

	private final OrderRepository orderRepository;
	private final OrderSchedulerHelper schedulerHelper;

	@Scheduled(fixedRate = 60_000)
	public void cancelExpiredUnpaidOrders() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
		int totalCancelled = 0;

		Slice<Order> slice;
		do {
			slice = orderRepository.findAllByStatusAndCreatedAtBefore(
				OrderStatus.UNPAID, cutoff, PageRequest.of(0, BATCH_SIZE));

			if (!slice.isEmpty()) {
				totalCancelled += schedulerHelper.cancelBatch(slice.getContent());
			}
		} while (slice.hasNext());

		if (totalCancelled > 0) {
			log.info("UNPAID 자동 취소: {}건 처리", totalCancelled);
		}
	}
}
