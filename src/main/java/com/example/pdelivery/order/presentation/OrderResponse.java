package com.example.pdelivery.order.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
	@Getter
	public static class OrderListData {
		UUID orderId;
		UUID storeId;
		String storeName;
		String deliveryAddress;
		String orderTitle;
		int totalPrice;
		String orderStatus;
		LocalDateTime createdAt;
		List<OrderLineResponse> orderMenus;

		public OrderListData(
			UUID orderId,
			String deliveryAddress,
			String orderTitle,
			int totalPrice,
			String orderStatus,
			LocalDateTime createdAt,
			List<OrderLineResponse> orderMenus
		) {
			this.orderId = orderId;
			this.deliveryAddress = deliveryAddress;
			this.orderTitle = orderTitle;
			this.totalPrice = totalPrice;
			this.orderStatus = orderStatus;
			this.createdAt = createdAt;
			this.orderMenus = orderMenus;
		}

		public void updateStoreInfo(UUID storeId, String storeName) {
			this.storeId = storeId;
			this.storeName = storeName;
		}

	}

	public record OrderLineResponse(
		String menuName,
		Integer price,
		Integer quantity
	) {

	}
}
