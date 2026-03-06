package com.example.pdelivery.menu.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.PageResponse;

public interface MenuService {

	MenuResponse createMenu(UUID storeId, MenuCreateRequest request);

	MenuResponse getMenu(UUID storeId, UUID menuId);

	PageResponse<MenuResponse> getMenus(UUID storeId, String keyword, Pageable pageable);

	MenuResponse updateMenu(UUID storeId, UUID menuId, MenuUpdateRequest request);

	void deleteMenu(UUID storeId, UUID menuId, UUID userId);
}
