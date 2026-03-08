package com.example.pdelivery.menu.application;

import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
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
import com.example.pdelivery.menu.presentation.dto.AiDescriptionHistoryResponse;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionRequest;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionResponse;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.ai.AiResponse;
import com.example.pdelivery.shared.ai.AiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

	private static final String MENU_DESCRIPTION_SYSTEM_PROMPT =
		"당신은 음식 메뉴 설명을 작성하는 전문가입니다. "
			+ "사용자가 제공하는 메뉴 정보를 바탕으로 매력적이고 간결한 메뉴 설명을 한국어로 작성하세요. "
			+ "설명은 50자 이내로 작성하세요.";

	private final MenuRepository menuRepository;
	private final MenuStoreRequirer menuStoreRequirer;
	private final AiService aiService;

	@Override
	public MenuResponse createMenu(UUID storeId, MenuCreateRequest request) {
		// TODO: StoreProvider 구현 후 스토어 존재 여부 및 소유권 검증
		menuStoreRequirer.getStore(storeId);

		MenuEntity menuEntity = MenuEntity.create(
			storeId, request.name(), request.price(), request.description(), null, request.aiRequestId());
		MenuEntity saved = menuRepository.save(menuEntity);

		return MenuResponse.from(saved);
	}

	@Override
	public AiDescriptionResponse generateAiDescription(UUID storeId, UUID menuId, UUID userId,
		AiDescriptionRequest request) {

		MenuEntity menuEntity = menuRepository.findByIdAndStoreId(menuId, storeId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

		String userPrompt = buildUserPrompt(menuEntity, request.requestText());

		try {
			AiResponse aiResponse = aiService.generate(userId, MENU_DESCRIPTION_SYSTEM_PROMPT, userPrompt);
			return new AiDescriptionResponse(aiResponse.content(), aiResponse.aiRequestId());
		} catch (Exception e) {
			log.error("AI 설명 생성 실패: menuId={}, userId={}", menuId, userId, e);
			throw new MenuException(MenuErrorCode.AI_GENERATION_FAILED, e);
		}
	}

	private String buildUserPrompt(MenuEntity menuEntity, String requestText) {
		Menu menu = menuEntity.getMenu();
		StringBuilder sb = new StringBuilder();
		sb.append("메뉴명: ").append(menu.getName());
		sb.append(", 가격: ").append(menu.getPrice()).append("원");

		if (menu.getDescription() != null && !menu.getDescription().isBlank()) {
			sb.append(", 기존 설명: ").append(menu.getDescription());
		}

		if (requestText != null && !requestText.isBlank()) {
			sb.append("\n요청 사항: ").append(requestText);
		}

		return sb.toString();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AiDescriptionHistoryResponse> getAiDescriptionHistory(UUID userId) {
		return aiService.getHistory(userId).stream()
			.map(AiDescriptionHistoryResponse::from)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public MenuResponse getMenu(UUID storeId, UUID menuId) {
		MenuEntity menuEntity = menuRepository.findByIdAndStoreId(menuId, storeId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

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
	public MenuResponse updateMenu(UUID storeId, UUID menuId, @NonNull MenuUpdateRequest request) {
		// TODO: StoreProvider 구현 후 스토어 존재 여부 및 소유권 검증
		menuStoreRequirer.getStore(storeId);

		MenuEntity menuEntity = menuRepository.findByIdAndStoreIdForUpdate(menuId, storeId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

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
	@Transactional(readOnly = true)
	public PageResponse<MenuResponse> searchMenus(String keyword, Pageable pageable) {
		Slice<MenuEntity> slice = menuRepository.searchByName(keyword.trim(), pageable);
		Slice<MenuResponse> responseSlice = slice.map(MenuResponse::from);
		return PageResponse.of(responseSlice);
	}

	@Override
	public void deleteMenu(UUID storeId, UUID menuId, UUID userId) {
		// TODO: StoreProvider 구현 후 스토어 존재 여부 및 소유권 검증
		menuStoreRequirer.getStore(storeId);

		MenuEntity menuEntity = menuRepository.findByIdAndStoreId(menuId, storeId)
			.orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

		menuEntity.softDelete(userId);
	}
}
