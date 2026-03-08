package com.example.pdelivery.order.application;

import static com.example.pdelivery.shared.enums.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

public class OrderRequestTest {
	@Test
	@DisplayName("주문 취소 실패 - 빈 취소 사유")
	public void cancelOrder_InvalidReason() {
		assertThatThrownBy(() -> new OrderRequest.OrderCancelRequest(""))
			.isInstanceOf(OrderException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.INVALID_REASON);
	}

	@Test
	@DisplayName("주문 거절 실패 - reason null")
	public void changeOrderStatus_RejectedFail() {
		assertThatThrownBy(() -> new OrderRequest.OrderChangeStatusRequest(REJECTED, null))
			.isInstanceOf(OrderException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.INVALID_REASON);
	}
}
