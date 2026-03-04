package com.example.pdelivery.order.domain;

import java.util.UUID;

import com.example.pdelivery.shared.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

	public OrderLine(String menuName, Integer quantity, Integer price) {
		this.menuName = menuName;
		this.quantity = quantity;
		this.price = price;
	}
}
