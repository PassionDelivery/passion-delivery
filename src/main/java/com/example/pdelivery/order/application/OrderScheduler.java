package com.example.pdelivery.order.application;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderScheduler {

	private final OrderSchedulerHelper schedulerHelper;

	@Scheduled(fixedRate = 60_000)
	public void cancelExpiredUnpaidOrders() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
		int totalCancelled = 0;

		do {
			int cancelled = schedulerHelper.cancelExpiredBatch(cutoff);
			totalCancelled += cancelled;
			if (cancelled == 0) {
				break;
			}
		} while (schedulerHelper.hasMore(cutoff));

		if (totalCancelled > 0) {
			log.info("UNPAID 자동 취소: {}건 처리", totalCancelled);
		}
	}
}
