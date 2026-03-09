package com.example.pdelivery.store.presentation.dto;

public record StoreStatusUpdateRequest(
	String status,
	String rejectCode,
	String rejectReason
) {
}
