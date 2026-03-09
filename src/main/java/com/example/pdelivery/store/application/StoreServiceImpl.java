package com.example.pdelivery.store.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.store.domain.StoreEntity;
import com.example.pdelivery.store.domain.StoreRepository;
import com.example.pdelivery.store.domain.StoreStatus;
import com.example.pdelivery.store.error.StoreErrorCode;
import com.example.pdelivery.store.error.StoreException;
import com.example.pdelivery.store.infrastructure.CategoryJpaRepository;
import com.example.pdelivery.store.presentation.dto.StoreCreateRequest;
import com.example.pdelivery.store.presentation.dto.StoreDetailResponse;
import com.example.pdelivery.store.presentation.dto.StoreResponse;
import com.example.pdelivery.store.presentation.dto.StoreSearchResponse;
import com.example.pdelivery.store.presentation.dto.StoreStatusUpdateRequest;
import com.example.pdelivery.store.presentation.dto.StoreUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public StoreResponse createStore(StoreCreateRequest request, UUID userId, boolean isManagerOrMaster) {
		if (!categoryJpaRepository.existsById(request.categoryId())) {
			throw new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND);
		}

		StoreStatus initialStatus = isManagerOrMaster ? StoreStatus.APPROVED : StoreStatus.PENDING;

		StoreEntity storeEntity = StoreEntity.create(
			userId,
			request.categoryId(),
			request.name(),
			request.address(),
			request.phone(),
			initialStatus
		);

		StoreEntity saved = storeRepository.save(storeEntity);
		return StoreResponse.from(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public StoreDetailResponse getStore(UUID storeId) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);
		if (storeEntity.getStatus() != StoreStatus.APPROVED) {
			throw new StoreException(StoreErrorCode.STORE_NOT_FOUND);
		}
		return StoreDetailResponse.from(storeEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<StoreSearchResponse> searchStores(String search, UUID categoryId, Pageable pageable) {
		Slice<StoreEntity> slice = storeRepository.searchByNameAndCategory(search, categoryId, pageable);
		Slice<StoreSearchResponse> result = slice.map(StoreSearchResponse::from);
		return PageResponse.of(result);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<StoreResponse> getStoresByStatus(StoreStatus status, Pageable pageable) {
		Slice<StoreEntity> slice = storeRepository.findByStatus(status, pageable);
		Slice<StoreResponse> result = slice.map(StoreResponse::from);
		return PageResponse.of(result);
	}

	@Override
	public StoreResponse updateStoreStatus(UUID storeId, StoreStatusUpdateRequest request) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);

		StoreStatus targetStatus;
		try {
			targetStatus = StoreStatus.valueOf(request.status());
		} catch (IllegalArgumentException e) {
			throw new StoreException(StoreErrorCode.INVALID_STATUS_CHANGE, e);
		}

		if (targetStatus == StoreStatus.APPROVED) {
			storeEntity.approve();
		} else if (targetStatus == StoreStatus.REJECTED) {
			storeEntity.reject(request.rejectCode(), request.rejectReason());
		} else {
			throw new StoreException(StoreErrorCode.INVALID_STATUS_CHANGE);
		}

		return StoreResponse.from(storeEntity);
	}

	@Override
	public StoreResponse updateStore(UUID storeId, StoreUpdateRequest request, UUID userId,
		boolean isManagerOrMaster) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);

		if (!isManagerOrMaster && !storeEntity.isOwnedBy(userId)) {
			throw new StoreException(StoreErrorCode.NOT_STORE_OWNER);
		}

		if (request.categoryId() != null && !categoryJpaRepository.existsById(request.categoryId())) {
			throw new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND);
		}

		storeEntity.updateInfo(request.categoryId(), request.name(), request.address(), request.phone());

		return StoreResponse.from(storeEntity);
	}

	@Override
	public void deleteStore(UUID storeId, UUID userId, boolean isManagerOrMaster) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);

		if (!isManagerOrMaster && !storeEntity.isOwnedBy(userId)) {
			throw new StoreException(StoreErrorCode.NOT_STORE_OWNER);
		}

		storeEntity.softDelete(userId);
	}

	private StoreEntity findStoreOrThrow(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
	}
}
