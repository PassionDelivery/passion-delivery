package com.example.pdelivery.order.domain;

import java.util.UUID;

import com.example.pdelivery.shared.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "p_order_line")
public class OrderLine extends AbstractEntity {
	@Column(nullable = false)
	private String menuName;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Integer price;

	private UUID menuId; // MSA 확장성 고려

	@Builder
	private OrderLine(String menuName, Integer quantity, Integer price, UUID menuId) {
		this.menuName = menuName;
		this.quantity = quantity;
		this.price = price;
		this.menuId = menuId;
	}

	public int calculateSubTotalPrice() {
		return this.price * this.quantity;
	}

	public OrderLineVO toVO() {
		return new OrderLineVO(
			this.menuId,
			this.menuName,
			this.quantity,
			this.price
		);
	}
}
