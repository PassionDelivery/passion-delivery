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

	@Column(name = "ai_request_id")
	private UUID aiRequestId;

	@Builder
	private MenuEntity(UUID storeId, Menu menu, UUID aiRequestId) {
		this.storeId = storeId;
		this.menu = menu;
		this.aiRequestId = aiRequestId;
	}

	public static MenuEntity create(UUID storeId, String name, Integer price, String description, Boolean isHidden,
		UUID aiRequestId) {
		Menu menu = new Menu(name, price, description, isHidden);
		return MenuEntity.builder()
			.storeId(storeId)
			.menu(menu)
			.aiRequestId(aiRequestId)
			.build();
	}

	public void updateMenu(Menu menu) {
		this.menu = menu;
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}
}