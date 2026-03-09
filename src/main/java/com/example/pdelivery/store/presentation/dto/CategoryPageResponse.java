package com.example.pdelivery.store.presentation.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record CategoryPageResponse<T>(
	List<T> content,
	long totalElements,
	int totalPages,
	int size,
	int number
) {

	public static <T> CategoryPageResponse<T> of(Page<T> page) {
		return new CategoryPageResponse<>(
			page.getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getSize(),
			page.getNumber()
		);
	}
}
