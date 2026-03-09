package com.example.pdelivery.store.domain;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_category")
public class CategoryEntity extends BaseEntity {

	@Column(name = "name", nullable = false, length = 10)
	private String name;

	public static CategoryEntity create(String name) {
		CategoryEntity category = new CategoryEntity();
		category.name = name;
		return category;
	}
}
