package com.example.pdelivery.order.domain;

import java.util.UUID;

import com.example.pdelivery.shared.enums.OrderStatus;
import com.example.pdelivery.shared.jpa.BaseEntityOnlyCreated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order_status_history")
@Entity
public class OrderStatusHistory extends BaseEntityOnlyCreated {

	@Column(nullable = false)
	private UUID orderId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus previousStatus;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus newStatus;

	public static OrderStatusHistory create(UUID orderId, OrderStatus previousStatus, OrderStatus newStatus) {
		OrderStatusHistory history = new OrderStatusHistory();
		history.orderId = orderId;
		history.previousStatus = previousStatus;
		history.newStatus = newStatus;
		return history;
	}
}
