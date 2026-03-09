package com.example.pdelivery.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.pdelivery.order.application.provider.OrderInfo;
import com.example.pdelivery.order.presentation.OrderResponse;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Getter
	private UUID storeId;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "order_id")
	private List<OrderLine> orderLines = new ArrayList<>(); // order 서버는 동일할 것 예상

	@Builder
	private Order(UUID storeId, String address, UUID customerId) {
		this.storeId = storeId;
		this.address = address;
		this.customerId = customerId;
		this.status = OrderStatus.PENDING;
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

	//작은 프로젝트라 res entity에서 바로 생성하지만 레이어 경계 다시 생각해보기
	public OrderResponse.OrderListData toSummaryResponse() {
		return new OrderResponse.OrderListData(
			this.getId(),
			this.address,
			generateOrderTitle(),
			this.totalPrice,
			this.status.name(),
			this.getCreatedAt(),
			this.orderLines.stream()
				.map(line -> {
					OrderLineVO orderLineVO = line.toVO();
					return new OrderResponse.OrderLineResponse(
						orderLineVO.menuName(),
						orderLineVO.price(),
						orderLineVO.quantity()
					);
				})
				.toList()
		);
	}

	private String generateOrderTitle() {
		if (orderLines.isEmpty()) {
			return "주문 내역 없음";
		}
		String firstItemName = orderLines.get(0).toVO().menuName();
		int extraCount = orderLines.size() - 1;
		return extraCount > 0 ? firstItemName + " 외 " + extraCount + "건" : firstItemName;
	}

	//OrderInfo는 application 단, 레이어 경계 다시 생각 필요성 있음
	public OrderInfo toOrderInfo() {
		return new OrderInfo(
			this.getId(),
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

	public void updateReason(String reason) {
		this.reason = reason;
	}

	public void updateStatus(OrderStatus status) {
		this.status = status;
	}

	public boolean checkCancellation() {
		return this.status.equals(OrderStatus.CANCELLED);
	}

	public boolean checkPending() {
		return this.status.equals(OrderStatus.PENDING);
	}

	public boolean checkCompleted() {
		return this.status.equals(OrderStatus.COMPLETED);
	}

	@Getter // test 위해서만
	public static class OrderView {
		private final String address;
		private final Integer totalPrice;
		private final OrderStatus status;
		private final List<OrderLine> orderLines;

		public OrderView(Order order) {
			this.address = order.address;
			this.totalPrice = order.totalPrice;
			this.orderLines = order.orderLines;
			this.status = order.status;
		}
	}

}
