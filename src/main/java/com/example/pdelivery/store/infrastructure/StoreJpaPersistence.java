package com.example.pdelivery.store.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.store.domain.StoreEntity;
import com.example.pdelivery.store.domain.StoreRepository;
import com.example.pdelivery.store.domain.StoreStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class StoreJpaPersistence implements StoreRepository {

	private final StoreJpaRepository storeJpaRepository;

	@Override
	public StoreEntity save(StoreEntity storeEntity) {
		return storeJpaRepository.save(storeEntity);
	}

	@Override
	public Optional<StoreEntity> findById(UUID storeId) {
		return storeJpaRepository.findByIdAndNotDeleted(storeId);
	}

	@Override
	public Slice<StoreEntity> searchByNameAndCategory(String keyword, UUID categoryId, Pageable pageable) {
		return storeJpaRepository.searchByNameAndCategory(keyword, categoryId, pageable);
	}

	@Override
	public Slice<StoreEntity> findByStatus(StoreStatus status, Pageable pageable) {
		return storeJpaRepository.findByStatus(status, pageable);
	}

	@Override
	public boolean existsById(UUID storeId) {
		return storeJpaRepository.findByIdAndNotDeleted(storeId).isPresent();
	}

	@Override
	public List<UUID> findStoreIdsByOwnerId(UUID ownerId) {
		return storeJpaRepository.findStoreIdsByOwnerId(ownerId);
	}
}
