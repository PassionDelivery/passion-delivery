package com.example.pdelivery.menu.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "p_menu")
public class MenuEntity extends BaseEntity {

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Embedded
	private Menu menu;

	@Builder
	private MenuEntity(UUID storeId, Menu menu) {
		this.storeId = storeId;
		this.menu = menu;
	}

	public static MenuEntity create(UUID storeId, String name, Integer price, String description) {
		Menu menu = new Menu(name, price, description);
		return MenuEntity.builder()
			.storeId(storeId)
			.menu(menu)
			.build();
	}

	public void updateMenu(Menu menu) {
		this.menu = menu;
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}
}