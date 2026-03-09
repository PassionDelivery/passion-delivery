package com.example.pdelivery.menu.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import com.example.pdelivery.menu.domain.MenuEntity;
import com.example.pdelivery.menu.domain.MenuRepository;
import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;
import com.example.pdelivery.menu.infrastructure.required.store.MenuStoreRequirer;
import com.example.pdelivery.menu.infrastructure.required.store.StoreData;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionRequest;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionResponse;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.ai.AiResponse;
import com.example.pdelivery.shared.ai.AiService;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private MenuStoreRequirer menuStoreRequirer;

	@Mock
	private AiService aiService;

	@InjectMocks
	private MenuServiceImpl menuService;

	private final UUID storeId = UUID.randomUUID();

	private final UUID userId = UUID.randomUUID();

	@Nested
	@DisplayName("메뉴 생성")
	class CreateMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 생성한다")
		void success() {
			MenuCreateRequest request = new MenuCreateRequest("치킨", 20000, "바삭한 치킨", null);
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨", null, null);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.save(any(MenuEntity.class))).willReturn(menuEntity);

			MenuResponse response = menuService.createMenu(storeId, request, userId);

			assertThat(response.name()).isEqualTo("치킨");
			assertThat(response.price()).isEqualTo(20000);
			assertThat(response.description()).isEqualTo("바삭한 치킨");
			assertThat(response.isHidden()).isFalse();
			then(menuRepository).should().save(any(MenuEntity.class));
		}

		@Test
		@DisplayName("다른 사용자의 aiRequestId를 사용하면 예외가 발생한다")
		void failAiRequestNotOwned() {
			UUID otherAiRequestId = UUID.randomUUID();
			MenuCreateRequest request = new MenuCreateRequest("치킨", 20000, "바삭한 치킨", otherAiRequestId);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(aiService.isOwnedByUser(otherAiRequestId, userId)).willReturn(false);

			assertThatThrownBy(() -> menuService.createMenu(storeId, request, userId))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException) e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.AI_REQUEST_NOT_OWNED);
				});
		}
	}

	@Nested
	@DisplayName("메뉴 단건 조회")
	class GetMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 조회한다")
		void success() {
			UUID menuId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨", null, null);

			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.of(menuEntity));

			MenuResponse response = menuService.getMenu(storeId, menuId);

			assertThat(response.name()).isEqualTo("치킨");
			assertThat(response.price()).isEqualTo(20000);
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 조회하면 예외가 발생한다")
		void failNotFound() {
			UUID menuId = UUID.randomUUID();
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> menuService.getMenu(storeId, menuId))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException) e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
				});
		}
	}

	@Nested
	@DisplayName("메뉴 목록 조회")
	class GetMenus {

		@Test
		@DisplayName("키워드 없이 메뉴 목록을 조회한다")
		void successWithoutKeyword() {
			Pageable pageable = PageRequest.of(0, 10);
			MenuEntity menu1 = MenuEntity.create(storeId, "치킨", 20000, null, null, null);
			MenuEntity menu2 = MenuEntity.create(storeId, "피자", 25000, null, null, null);

			given(menuRepository.findAllByStoreId(storeId, pageable))
				.willReturn(new SliceImpl<>(List.of(menu1, menu2), pageable, false));

			PageResponse<MenuResponse> response = menuService.getMenus(storeId, null, pageable);

			assertThat(response.contents()).hasSize(2);
			assertThat(response.hasNext()).isFalse();
		}

		@Test
		@DisplayName("키워드로 메뉴를 검색한다")
		void successWithKeyword() {
			Pageable pageable = PageRequest.of(0, 10);
			MenuEntity menu1 = MenuEntity.create(storeId, "양념치킨", 22000, null, null, null);

			given(menuRepository.searchByStoreIdAndName(storeId, "치킨", pageable))
				.willReturn(new SliceImpl<>(List.of(menu1), pageable, false));

			PageResponse<MenuResponse> response = menuService.getMenus(storeId, "치킨", pageable);

			assertThat(response.contents()).hasSize(1);
			assertThat(response.contents().get(0).name()).isEqualTo("양념치킨");
		}
	}

	@Nested
	@DisplayName("메뉴 수정")
	class UpdateMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 수정한다")
		void success() {
			UUID menuId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨", null, null);
			MenuUpdateRequest request = new MenuUpdateRequest("양념치킨", 22000, "매콤한 양념치킨", false);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreIdForUpdate(menuId, storeId)).willReturn(Optional.of(menuEntity));

			MenuResponse response = menuService.updateMenu(storeId, menuId, request);

			assertThat(response.name()).isEqualTo("양념치킨");
			assertThat(response.price()).isEqualTo(22000);
			assertThat(response.description()).isEqualTo("매콤한 양념치킨");
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 수정하면 예외가 발생한다")
		void failNotFound() {
			UUID menuId = UUID.randomUUID();
			MenuUpdateRequest request = new MenuUpdateRequest("양념치킨", 22000, "매콤한 양념치킨", false);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreIdForUpdate(menuId, storeId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> menuService.updateMenu(storeId, menuId, request))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException) e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
				});
		}
	}

	@Nested
	@DisplayName("메뉴 삭제")
	class DeleteMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 삭제한다")
		void success() {
			UUID menuId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, null, null, null);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.of(menuEntity));

			menuService.deleteMenu(storeId, menuId, userId);

			assertThat(menuEntity.isDeleted()).isTrue();
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 삭제하면 예외가 발생한다")
		void failNotFound() {
			UUID menuId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> menuService.deleteMenu(storeId, menuId, userId))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException) e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
				});
		}
	}

	@Nested
	@DisplayName("전체 메뉴 검색")
	class SearchMenus {

		@Test
		@DisplayName("키워드로 전체 메뉴를 검색한다")
		void success() {
			Pageable pageable = PageRequest.of(0, 10);
			MenuEntity menu1 = MenuEntity.create(storeId, "소고기탕수육", 18000, null, null, null);
			MenuEntity menu2 = MenuEntity.create(UUID.randomUUID(), "탕수육", 15000, null, null, null);

			given(menuRepository.searchByName("탕수", pageable))
				.willReturn(new SliceImpl<>(List.of(menu1, menu2), pageable, false));

			PageResponse<MenuResponse> response = menuService.searchMenus("탕수", pageable);

			assertThat(response.contents()).hasSize(2);
			assertThat(response.contents().get(0).name()).isEqualTo("소고기탕수육");
			assertThat(response.contents().get(1).name()).isEqualTo("탕수육");
			assertThat(response.hasNext()).isFalse();
		}

		@Test
		@DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
		void successEmpty() {
			Pageable pageable = PageRequest.of(0, 10);

			given(menuRepository.searchByName("존재하지않는메뉴", pageable))
				.willReturn(new SliceImpl<>(List.of(), pageable, false));

			PageResponse<MenuResponse> response = menuService.searchMenus("존재하지않는메뉴", pageable);

			assertThat(response.contents()).isEmpty();
			assertThat(response.hasNext()).isFalse();
		}
	}

	@Nested
	@DisplayName("AI 메뉴 설명 생성")
	class GenerateAiDescription {

		@Test
		@DisplayName("메뉴 정보를 바탕으로 AI 설명을 생성한다")
		void success() {
			UUID menuId = UUID.randomUUID();
			UUID aiRequestId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "후라이드 치킨", 20000, "바삭한 치킨", null, null);
			AiDescriptionRequest request = new AiDescriptionRequest("바삭한 느낌 강조해줘");

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.of(menuEntity));
			given(aiService.generate(eq(userId), anyString(), anyString()))
				.willReturn(new AiResponse(aiRequestId, "겉은 바삭하고 속은 촉촉한 프리미엄 후라이드 치킨"));

			AiDescriptionResponse response = menuService.generateAiDescription(storeId, menuId, userId, request);

			assertThat(response.description()).isEqualTo("겉은 바삭하고 속은 촉촉한 프리미엄 후라이드 치킨");
			assertThat(response.aiRequestId()).isEqualTo(aiRequestId);
			then(aiService).should().generate(eq(userId), anyString(), argThat(prompt -> prompt.contains("후라이드 치킨")));
		}

		@Test
		@DisplayName("요청 사항 없이도 메뉴 정보만으로 AI 설명을 생성한다")
		void successWithoutRequestText() {
			UUID menuId = UUID.randomUUID();
			UUID aiRequestId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "양념치킨", 22000, null, null, null);
			AiDescriptionRequest request = new AiDescriptionRequest(null);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.of(menuEntity));
			given(aiService.generate(eq(userId), anyString(), anyString()))
				.willReturn(new AiResponse(aiRequestId, "달콤 매콤한 양념이 일품인 양념치킨"));

			AiDescriptionResponse response = menuService.generateAiDescription(storeId, menuId, userId, request);

			assertThat(response.description()).isEqualTo("달콤 매콤한 양념이 일품인 양념치킨");
		}

		@Test
		@DisplayName("존재하지 않는 메뉴에 대해 AI 설명 생성 시 예외가 발생한다")
		void failMenuNotFound() {
			UUID menuId = UUID.randomUUID();
			AiDescriptionRequest request = new AiDescriptionRequest(null);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> menuService.generateAiDescription(storeId, menuId, userId, request))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException)e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
				});
		}

		@Test
		@DisplayName("AI 서비스 호출 실패 시 예외가 발생한다")
		void failOnAiServiceError() {
			UUID menuId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, null, null, null);
			AiDescriptionRequest request = new AiDescriptionRequest(null);

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId, UUID.randomUUID()));
			given(menuRepository.findByIdAndStoreId(menuId, storeId)).willReturn(Optional.of(menuEntity));
			given(aiService.generate(eq(userId), anyString(), anyString()))
				.willThrow(new RuntimeException("AI 서비스 오류"));

			assertThatThrownBy(() -> menuService.generateAiDescription(storeId, menuId, userId, request))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException)e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.AI_GENERATION_FAILED);
				});
		}
	}
}
