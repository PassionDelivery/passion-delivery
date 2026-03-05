package com.example.pdelivery.menu.presentation.dto;

public record MenuUpdateRequest(
	String name,
	Integer price,
	String description,
	Boolean isHidden
) {
}
