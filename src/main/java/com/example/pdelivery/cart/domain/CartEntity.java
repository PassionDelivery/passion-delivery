package com.example.pdelivery.cart.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.pdelivery.cart.error.CartErrorCode;
import com.example.pdelivery.cart.error.CartException;
import com.example.pdelivery.shared.AbstractEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_cart")
public class CartEntity extends AbstractEntity {

	@Column(name = "user_id", nullable = false, unique = true)
	private UUID userId;

	@Column(name = "store_id")
	private UUID storeId;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "p_cart_line", joinColumns = @JoinColumn(name = "cart_id"))
	private List<CartLineEntity> cartLineEntities = new ArrayList<>();

	public static CartEntity create(UUID userId, UUID storeId) {
		CartEntity cart = new CartEntity();
		cart.userId = userId;
		cart.storeId = storeId;
		return cart;
	}

	public void addOrUpdateItem(UUID menuId, int quantity) {
		CartLineEntity existing = cartLineEntities.stream()
			.filter(line -> line.menuId().equals(menuId))
			.findFirst()
			.orElse(null);

		if (existing != null) {
			cartLineEntities.remove(existing);
			cartLineEntities.add(new CartLineEntity(existing.no(), menuId, quantity));
		} else {
			int nextNo = cartLineEntities.stream()
				.mapToInt(CartLineEntity::no)
				.max()
				.orElse(0) + 1;
			cartLineEntities.add(new CartLineEntity(nextNo, menuId, quantity));
		}
	}

	public void updateItemQuantity(UUID menuId, int quantity) {
		CartLineEntity existing = cartLineEntities.stream()
			.filter(line -> line.menuId().equals(menuId))
			.findFirst()
			.orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_FOUND));
		cartLineEntities.remove(existing);
		cartLineEntities.add(new CartLineEntity(existing.no(), menuId, quantity));
	}

	public void removeItem(UUID menuId) {
		boolean removed = cartLineEntities.removeIf(line -> line.menuId().equals(menuId));
		if (!removed) {
			throw new CartException(CartErrorCode.CART_ITEM_NOT_FOUND);
		}
	}

	public void resetForStore(UUID newStoreId) {
		this.storeId = newStoreId;
		this.cartLineEntities.clear();
	}

	public boolean isSameStore(UUID storeId) {
		return this.storeId != null && this.storeId.equals(storeId);
	}

	public boolean isEmpty() {
		return cartLineEntities == null || cartLineEntities.isEmpty();
	}
}
