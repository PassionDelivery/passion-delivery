package com.example.pdelivery.menu.infrastructure;

import java.util.Optional;
import java.util.UUID;

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
	public MenuEntity save(MenuEntity menuEntity) {
		return menuJpaRepository.save(menuEntity);
	}

	@Override
	public Optional<MenuEntity> findByIdForUpdate(UUID menuId) {
		return menuJpaRepository.findByIdForUpdate(menuId);
	}

	@Override
	public boolean existsById(UUID menuId) {
		return menuJpaRepository.existsById(menuId);
	}
}