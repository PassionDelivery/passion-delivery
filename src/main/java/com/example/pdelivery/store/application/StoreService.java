package com.example.pdelivery.store.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.store.domain.StoreStatus;
import com.example.pdelivery.store.presentation.dto.StoreCreateRequest;
import com.example.pdelivery.store.presentation.dto.StoreDetailResponse;
import com.example.pdelivery.store.presentation.dto.StoreResponse;
import com.example.pdelivery.store.presentation.dto.StoreSearchResponse;
import com.example.pdelivery.store.presentation.dto.StoreStatusUpdateRequest;
import com.example.pdelivery.store.presentation.dto.StoreUpdateRequest;

public interface StoreService {

	// OWNER: 본인 가게 등록 (PENDING)
	StoreResponse createMyStore(StoreCreateRequest request, UUID ownerId);

	// MANAGER/MASTER: 가게 등록 (APPROVED)
	StoreResponse createStore(StoreCreateRequest request, UUID adminId);

	StoreDetailResponse getStore(UUID storeId);

	PageResponse<StoreSearchResponse> searchStores(String search, UUID categoryId, Pageable pageable);

	PageResponse<StoreResponse> getStoresByStatus(StoreStatus status, Pageable pageable);

	StoreResponse updateStoreStatus(UUID storeId, StoreStatusUpdateRequest request);

	// OWNER: 본인 가게만 수정
	StoreResponse updateMyStore(UUID storeId, StoreUpdateRequest request, UUID ownerId);

	// MANAGER/MASTER: 모든 가게 수정
	StoreResponse updateStore(UUID storeId, StoreUpdateRequest request, UUID adminId);

	// OWNER: 본인 가게만 삭제
	void deleteMyStore(UUID storeId, UUID ownerId);

	// MANAGER/MASTER: 모든 가게 삭제
	void deleteStore(UUID storeId, UUID adminId);
}
