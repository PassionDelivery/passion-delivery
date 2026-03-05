package com.example.pdelivery.cart.domain;

import java.util.List;
import java.util.UUID;

import com.example.pdelivery.shared.AbstractEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "p_cart")
public class CartEntity extends AbstractEntity {
	@Column(name = "user_id", nullable = false, unique = true)
	private UUID userId;
	@Column(name = "store_id")
	private UUID storeId;
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "p_cart_line", joinColumns = @JoinColumn(name = "cart_id"))
	@OrderColumn(name = "no")
	private List<CartLineEntity> cartLineEntities;
}
