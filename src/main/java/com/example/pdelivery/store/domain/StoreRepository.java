package com.example.pdelivery.store.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StoreRepository {

	StoreEntity save(StoreEntity storeEntity);

	Optional<StoreEntity> findById(UUID storeId);

	Slice<StoreEntity> searchByNameAndCategory(String keyword, UUID categoryId, Pageable pageable);

	Slice<StoreEntity> findByStatus(StoreStatus status, Pageable pageable);

	boolean existsById(UUID storeId);
}
