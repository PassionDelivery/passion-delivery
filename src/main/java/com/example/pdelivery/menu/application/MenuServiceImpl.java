package com.example.pdelivery.menu.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.menu.domain.Menu;
import com.example.pdelivery.menu.domain.MenuEntity;
import com.example.pdelivery.menu.domain.MenuRepository;
import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;
import com.example.pdelivery.menu.infrastructure.required.store.MenuStoreRequirer;

import lombok.extern.slf4j.Slf4j;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;
	private final UserRepository userRepository;
	private final MenuStoreRequirer menuStoreRequirer;

	@Override
	public MenuResponse createMenu(UUID storeId, MenuCreateRequest request) {
		menuStoreRequirer.getStore(storeId);

		if (Boolean.TRUE.equals(request.useAiDescription())) {
			log.warn("AI 설명 생성이 요청되었지만 아직 구현되지 않았습니다. storeId={}", storeId);
		}

		MenuEntity menuEntity;
		try {
			menuEntity = MenuEntity.create(storeId, request.name(), request.price(), request.description());
		} catch (IllegalArgumentException e) {
			throw toMenuException(e);
		}

		MenuEntity saved = menuRepository.save(menuEntity);

		return MenuResponse.from(saved);
	}

	private MenuException toMenuException(IllegalArgumentException e) {
		String msg = e.getMessage();
		if (msg != null && msg.contains("이름")) {
			return new MenuException(MenuErrorCode.INVALID_MENU_NAME);
		}
		if (msg != null && msg.contains("설명")) {
			return new MenuException(MenuErrorCode.INVALID_MENU_DESCRIPTION);
		}
		return new MenuException(MenuErrorCode.INVALID_MENU_PRICE);
	}

	@Override
	@Transactional(readOnly = true)
	public MenuResponse getMenu(UUID storeId, UUID menuId) {
		MenuEntity menuEntity = findMenuOrThrow(menuId);
		validateNotDeleted(menuEntity);

		return MenuResponse.from(menuEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<MenuResponse> getMenus(UUID storeId, String keyword, Pageable pageable) {
		Slice<MenuEntity> slice;

		if (keyword != null && !keyword.isBlank()) {
			slice = menuRepository.searchByStoreIdAndName(storeId, keyword.trim(), pageable);
		} else {
			slice = menuRepository.findAllByStoreId(storeId, pageable);
		}

		Slice<MenuResponse> responseSlice = slice.map(MenuResponse::from);
		return PageResponse.of(responseSlice);
	}

	@Override
	public MenuResponse updateMenu(UUID storeId, UUID menuId, MenuUpdateRequest request) {
		MenuEntity menuEntity = findMenuForUpdateOrThrow(menuId);
		validateNotDeleted(menuEntity);

		Menu updatedMenu = new Menu(
			request.name(),
			request.price(),
			request.description(),
			request.isHidden()
		);

		menuEntity.updateMenu(updatedMenu);

		return MenuResponse.from(menuEntity);
	}

	@Override
	public void deleteMenu(UUID storeId, UUID menuId, String username) {
		MenuEntity menuEntity = findMenuOrThrow(menuId);
		validateNotDeleted(menuEntity);

		UserEntity user = userRepository.findByUsername(username)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

		menuEntity.softDelete(user.getId());
	}

	private MenuEntity findMenuOrThrow(UUID menuId) {
		return menuRepository.findById(menuId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));
	}

	private MenuEntity findMenuForUpdateOrThrow(UUID menuId) {
		return menuRepository.findByIdForUpdate(menuId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));
	}

	private void validateNotDeleted(MenuEntity menuEntity) {
		if (menuEntity.isDeleted()) {
			throw new MenuException(MenuErrorCode.MENU_ALREADY_DELETED);
		}
	}
}
