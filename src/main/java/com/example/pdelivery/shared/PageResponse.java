package com.example.pdelivery.shared;

import java.util.List;

import org.springframework.data.domain.Slice;

public record PageResponse<T>(
	List<T> contents,
	boolean hasNext
) {
	public static <T> PageResponse<T> of(Slice<T> data) {
		return new PageResponse<>(data.getContent(), data.hasNext());
	}
}
