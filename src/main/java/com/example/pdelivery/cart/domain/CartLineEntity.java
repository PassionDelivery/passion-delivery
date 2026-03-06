package com.example.pdelivery.cart.domain;

import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable
public record CartLineEntity(
	Integer no,
	UUID menuId,
	Integer quantity
) {
}
