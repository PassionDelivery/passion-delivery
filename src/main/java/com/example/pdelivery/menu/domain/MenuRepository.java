package com.example.pdelivery.menu.domain;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

	Optional<MenuEntity> findById(UUID menuId);

	Optional<MenuEntity> findByIdForUpdate(UUID menuId);

	MenuEntity save(MenuEntity menuEntity);

	boolean existsById(UUID menuId);
}