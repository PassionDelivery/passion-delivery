package com.example.pdelivery.order.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.pdelivery.shared.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
	@Getter
	public static class OrderCreateResponse {
		UUID orderId;
		String orderStatus;

		public OrderCreateResponse(UUID orderId, String orderStatus) {
			this.orderId = orderId;
			this.orderStatus = orderStatus;
		}
	}

	@Getter
	public static class OrderDataResponse {
		UUID orderId;
		UUID storeId;
		String storeName;
		String username;
		String deliveryAddress;
		String orderTitle;
		Long totalPrice;
		String orderStatus;
		LocalDateTime createdAt;
		List<OrderLineResponse> orderMenus;

		public OrderDataResponse(
			UUID orderId,
			String deliveryAddress,
			String orderTitle,
			Long totalPrice,
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

		public void updateUsername(String username) {
			this.username = username;
		}

	}

	public record OrderLineResponse(
		String menuName,
		Integer price,
		Integer quantity
	) {

	}

	public static class OrderStatusResponse {
		UUID orderId;
		OrderStatus status;

		public OrderStatusResponse(UUID orderId, OrderStatus status) {
			this.orderId = orderId;
			this.status = status;
		}
	}

}
