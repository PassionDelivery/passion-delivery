package com.example.pdelivery.menu.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenuRepository {

	Optional<MenuEntity> findById(UUID menuId);

	Optional<MenuEntity> findByIdForUpdate(UUID menuId);

	MenuEntity save(MenuEntity menuEntity);

	boolean existsById(UUID menuId);

	Slice<MenuEntity> findAllByStoreId(UUID storeId, Pageable pageable);

	Slice<MenuEntity> searchByStoreIdAndName(UUID storeId, String keyword, Pageable pageable);
}