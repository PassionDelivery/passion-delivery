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

	StoreResponse createStore(StoreCreateRequest request, UUID userId, boolean isManagerOrMaster);

	StoreDetailResponse getStore(UUID storeId);

	PageResponse<StoreSearchResponse> searchStores(String search, UUID categoryId, Pageable pageable);

	PageResponse<StoreResponse> getStoresByStatus(StoreStatus status, Pageable pageable);

	StoreResponse updateStoreStatus(UUID storeId, StoreStatusUpdateRequest request);

	StoreResponse updateStore(UUID storeId, StoreUpdateRequest request, UUID userId, boolean isManagerOrMaster);

	void deleteStore(UUID storeId, UUID userId, boolean isManagerOrMaster);
}
