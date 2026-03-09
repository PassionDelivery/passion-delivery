package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.error.OrderErrorCode.*;

import java.util.UUID;

import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.shared.enums.OrderStatus;

public class OrderRequest {
	public record OrderCreateRequest(UUID cartId, String address) {

		public OrderCreateRequest {
			if (cartId == null) {
				throw new OrderException(REQUIRED_PARAMETER_MISSING, "cartId is missing");
			}
			if (address == null || address.isBlank()) {
				throw new OrderException(REQUIRED_PARAMETER_MISSING, "cartId is missing");
			}
		}
	}

	public record OrderCancelRequest(String reason) {
		public OrderCancelRequest {
			if (reason == null || reason.isBlank()) {
				throw new OrderException(INVALID_REASON);
			}
		}
	}

	public record OrderChangeStatusRequest(OrderStatus orderStatus, String reason) {
		public OrderChangeStatusRequest {
			if (orderStatus == null) {
				throw new OrderException(REQUIRED_PARAMETER_MISSING, "주문 상태 입력이 필요합니다.");
			}
			if (orderStatus.equals(OrderStatus.REJECTED) && (reason == null || reason.isBlank())) {
				throw new OrderException(INVALID_REASON);
			}
		}
	}
}
