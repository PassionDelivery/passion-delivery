package com.example.pdelivery.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.pdelivery.order.application.provider.OrderInfo;
import com.example.pdelivery.shared.BaseEntity;
import com.example.pdelivery.shared.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Table(name = "p_order")
@Entity
public class Order extends BaseEntity {
	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderType orderType;

	private String reason;

	@Getter
	private Integer totalPrice;

	//MSA 확장 고려하여 논리적 연관관계 맵핑
	private UUID customerId;

	private UUID storeId;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "order_id")
	private List<OrderLine> orderLines = new ArrayList<>(); // order 서버는 동일할 것 예상

	@Builder
	private Order(UUID storeId, String address, UUID customerId) {
		this.storeId = storeId;
		this.address = address;
		this.customerId = customerId;
		this.status = OrderStatus.ACCEPTED;
		this.orderType = OrderType.ONLINE;
	}

	private void addOrderLine(OrderLineVO orderLineVO) {
		OrderLine orderLine = OrderLine.builder()
			.menuId(orderLineVO.menuId())
			.menuName(orderLineVO.menuName())
			.quantity(orderLineVO.quantity())
			.price(orderLineVO.price())
			.build();
		this.orderLines.add(orderLine);
	}

	private void calculateTotalPrice() {
		this.totalPrice = this.orderLines.stream()
			.mapToInt(line -> line.calculateSubTotalPrice())
			.sum();
	}

	public static Order create(UUID storeId, String address, UUID customerId, List<OrderLineVO> items) {
		Order order = Order.builder()
			.storeId(storeId)
			.address(address)
			.customerId(customerId)
			.build();
		for (OrderLineVO item : items) {
			order.addOrderLine(item);
		}

		order.calculateTotalPrice();

		return order;
	}

	public OrderInfo toOrderInfo() {
		return new OrderInfo(
			this.getId(), // BaseEntity에 getId()가 있다고 가정
			this.storeId,
			this.customerId,
			this.address,
			this.status,
			this.reason,
			this.totalPrice,
			this.orderLines.stream()
				.map(line -> line.toVO())
				.toList()
		);
	}

	@Getter // test 위해서만
	public static class OrderView {
		private final String address;
		private final Integer totalPrice;
		private final List<OrderLine> orderLines;

		public OrderView(Order order) {
			this.address = order.address;
			this.totalPrice = order.totalPrice;
			this.orderLines = order.orderLines;
		}
	}

}
