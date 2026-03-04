package com.example.pdelivery.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
	
	private Integer totalPrice;

	//MSA 확장 고려하여 논리적 연관관계 맵핑
	private String customerId;

	private UUID storeId;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "order_id")
	private List<OrderLine> orderLines = new ArrayList<>(); // order 서버는 동일할 것 예상

	public Order(String address) {
		this.address = address;
		this.status = OrderStatus.ACCEPTED;
		this.orderType = OrderType.ONLINE;
	}

	public void addOrderLines(OrderLine orderLine) {
		orderLines.add(orderLine);
	}
}
