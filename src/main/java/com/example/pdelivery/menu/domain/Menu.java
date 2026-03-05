package com.example.pdelivery.menu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "price", nullable = false)
	private Integer price;

	// NOTE: If you prefer keeping isHidden on MenuEntity instead of the embeddable,
	// you can remove this field and manage it in MenuEntity.
	@Column(name = "is_hidden", nullable = false)
	private Boolean isHidden;

	public Menu(String name, Integer price, String description) {
		this(name, price, description, false);
	}

	public Menu(String name, Integer price, String description, Boolean isHidden) {
		if (name == null || name.isBlank() || name.length() > 100) {
			throw new IllegalArgumentException("메뉴 이름은 공백이면 안되고, 100자 이하여야 합니다.");
		}

		if (description != null && description.length() > 500) {
			throw new IllegalArgumentException("메뉴 설명은 500자 이하여야 합니다.");
		}

		if (price == null || price < 0) {
			throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
		}

		this.name = name;
		this.price = price;
		this.description = description;
		this.isHidden = (isHidden != null) ? isHidden : false;
	}
}