package com.example.pdelivery.menu.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "p_menu")
public class MenuEntity extends BaseEntity {

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Embedded
	private Menu menu;

	protected MenuEntity() {
	}

	public MenuEntity(UUID storeId, Menu menu) {
		this.storeId = storeId;
		this.menu = menu;
	}
}