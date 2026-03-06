package com.example.pdelivery.menu.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.menu.domain.MenuEntity;
import com.example.pdelivery.menu.domain.MenuRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MenuJpaPersistence implements MenuRepository {

	private final MenuJpaRepository menuJpaRepository;

	@Override
	public Optional<MenuEntity> findById(UUID menuId) {
		return menuJpaRepository.findById(menuId);
	}

	@Override
	public List<MenuEntity> findAllByIdIn(List<UUID> menuIds) {
		return menuJpaRepository.findAllById(menuIds);
	}

	@Override
	public Optional<MenuEntity> findByIdAndStoreId(UUID menuId, UUID storeId) {
		return menuJpaRepository.findByIdAndStoreId(menuId, storeId);
	}

	@Override
	public Optional<MenuEntity> findByIdAndStoreIdForUpdate(UUID menuId, UUID storeId) {
		return menuJpaRepository.findByIdAndStoreIdForUpdate(menuId, storeId);
	}

	@Override
	public MenuEntity save(MenuEntity menuEntity) {
		return menuJpaRepository.save(menuEntity);
	}

	@Override
	public boolean existsById(UUID menuId) {
		return menuJpaRepository.existsById(menuId);
	}

	@Override
	public Slice<MenuEntity> findAllByStoreId(UUID storeId, Pageable pageable) {
		return menuJpaRepository.findAllByStoreId(storeId, pageable);
	}

	@Override
	public Slice<MenuEntity> searchByStoreIdAndName(UUID storeId, String keyword, Pageable pageable) {
		return menuJpaRepository.searchByStoreIdAndName(storeId, keyword, pageable);
	}
}
