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
	public StoreResponse createMyStore(StoreCreateRequest request, UUID ownerId) {
		validateCategory(request.categoryId());
		StoreEntity saved = storeRepository.save(
			StoreEntity.create(ownerId, request.categoryId(), request.name(), request.address(), request.phone(),
				StoreStatus.PENDING));
		return StoreResponse.from(saved);
	}

	@Override
	public StoreResponse createStore(StoreCreateRequest request, UUID adminId) {
		validateCategory(request.categoryId());
		StoreEntity saved = storeRepository.save(
			StoreEntity.create(adminId, request.categoryId(), request.name(), request.address(), request.phone(),
				StoreStatus.APPROVED));
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
		return PageResponse.of(slice.map(StoreSearchResponse::from));
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<StoreResponse> getStoresByStatus(StoreStatus status, Pageable pageable) {
		Slice<StoreEntity> slice = storeRepository.findByStatus(status, pageable);
		return PageResponse.of(slice.map(StoreResponse::from));
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
	public StoreResponse updateMyStore(UUID storeId, StoreUpdateRequest request, UUID ownerId) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);
		if (!storeEntity.isOwnedBy(ownerId)) {
			throw new StoreException(StoreErrorCode.NOT_STORE_OWNER);
		}
		applyUpdate(storeEntity, request);
		return StoreResponse.from(storeEntity);
	}

	@Override
	public StoreResponse updateStore(UUID storeId, StoreUpdateRequest request, UUID adminId) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);
		applyUpdate(storeEntity, request);
		return StoreResponse.from(storeEntity);
	}

	@Override
	public void deleteMyStore(UUID storeId, UUID ownerId) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);
		if (!storeEntity.isOwnedBy(ownerId)) {
			throw new StoreException(StoreErrorCode.NOT_STORE_OWNER);
		}
		storeEntity.softDelete(ownerId);
	}

	@Override
	public void deleteStore(UUID storeId, UUID adminId) {
		StoreEntity storeEntity = findStoreOrThrow(storeId);
		storeEntity.softDelete(adminId);
	}

	private void validateCategory(UUID categoryId) {
		if (!categoryJpaRepository.existsById(categoryId)) {
			throw new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND);
		}
	}

	private void applyUpdate(StoreEntity storeEntity, StoreUpdateRequest request) {
		if (request.categoryId() != null) {
			validateCategory(request.categoryId());
		}
		storeEntity.updateInfo(request.categoryId(), request.name(), request.address(), request.phone());
	}

	private StoreEntity findStoreOrThrow(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
	}
}
