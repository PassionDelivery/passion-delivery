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
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.menu.infrastructure.required.store.MenuStoreRequirer;
import com.example.pdelivery.menu.infrastructure.required.store.StoreData;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MenuStoreRequirer menuStoreRequirer;

	@InjectMocks
	private MenuServiceImpl menuService;

	private final UUID storeId = UUID.randomUUID();

	@Nested
	@DisplayName("메뉴 생성")
	class CreateMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 생성한다")
		void success() {
			MenuCreateRequest request = new MenuCreateRequest("치킨", 20000, "바삭한 치킨", null, null);
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨");

			given(menuStoreRequirer.getStore(storeId)).willReturn(new StoreData(storeId));
			given(menuRepository.save(any(MenuEntity.class))).willReturn(menuEntity);

			MenuResponse response = menuService.createMenu(storeId, request);

			assertThat(response.name()).isEqualTo("치킨");
			assertThat(response.price()).isEqualTo(20000);
			assertThat(response.description()).isEqualTo("바삭한 치킨");
			assertThat(response.isHidden()).isFalse();
			then(menuRepository).should().save(any(MenuEntity.class));
		}
	}

	@Nested
	@DisplayName("메뉴 단건 조회")
	class GetMenu {

		@Test
		@DisplayName("메뉴를 정상적으로 조회한다")
		void success() {
			UUID menuId = UUID.randomUUID();
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨");

			given(menuRepository.findById(menuId)).willReturn(Optional.of(menuEntity));

			MenuResponse response = menuService.getMenu(storeId, menuId);

			assertThat(response.name()).isEqualTo("치킨");
			assertThat(response.price()).isEqualTo(20000);
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 조회하면 예외가 발생한다")
		void failNotFound() {
			UUID menuId = UUID.randomUUID();
			given(menuRepository.findById(menuId)).willReturn(Optional.empty());

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
			MenuEntity menu1 = MenuEntity.create(storeId, "치킨", 20000, null);
			MenuEntity menu2 = MenuEntity.create(storeId, "피자", 25000, null);

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
			MenuEntity menu1 = MenuEntity.create(storeId, "양념치킨", 22000, null);

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
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨");
			MenuUpdateRequest request = new MenuUpdateRequest("양념치킨", 22000, "매콤한 양념치킨", false);

			given(menuRepository.findByIdForUpdate(menuId)).willReturn(Optional.of(menuEntity));

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

			given(menuRepository.findByIdForUpdate(menuId)).willReturn(Optional.empty());

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
			String username = "test_user";
			MenuEntity menuEntity = MenuEntity.create(storeId, "치킨", 20000, null);
			UserEntity user = mock(UserEntity.class);
			given(user.getId()).willReturn(userId);

			given(menuRepository.findById(menuId)).willReturn(Optional.of(menuEntity));
			given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

			menuService.deleteMenu(storeId, menuId, username);

			assertThat(menuEntity.isDeleted()).isTrue();
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 삭제하면 예외가 발생한다")
		void failNotFound() {
			UUID menuId = UUID.randomUUID();
			String username = "test_user";

			given(menuRepository.findById(menuId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> menuService.deleteMenu(storeId, menuId, username))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> {
					MenuException me = (MenuException) e;
					assertThat(me.getErrorCode()).isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
				});
		}
	}
}
