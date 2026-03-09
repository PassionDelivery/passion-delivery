package com.example.pdelivery.store.application;

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

import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.store.domain.StoreEntity;
import com.example.pdelivery.store.domain.StoreRepository;
import com.example.pdelivery.store.domain.StoreStatus;
import com.example.pdelivery.store.error.StoreErrorCode;
import com.example.pdelivery.store.error.StoreException;
import com.example.pdelivery.store.infrastructure.CategoryJpaRepository;
import com.example.pdelivery.store.presentation.dto.StoreCreateRequest;
import com.example.pdelivery.store.presentation.dto.StoreDetailResponse;
import com.example.pdelivery.store.presentation.dto.StoreResponse;
import com.example.pdelivery.store.presentation.dto.StoreSearchResponse;
import com.example.pdelivery.store.presentation.dto.StoreStatusUpdateRequest;
import com.example.pdelivery.store.presentation.dto.StoreUpdateRequest;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private CategoryJpaRepository categoryJpaRepository;

	@InjectMocks
	private StoreServiceImpl storeService;

	private final UUID userId = UUID.randomUUID();
	private final UUID categoryId = UUID.randomUUID();

	@Nested
	@DisplayName("가게 생성")
	class CreateStore {

		@Test
		@DisplayName("OWNER가 가게를 생성하면 PENDING 상태가 된다")
		void successOwner() {
			StoreCreateRequest request = new StoreCreateRequest(categoryId, "치킨집", "서울시 강남구", "02-1234-5678");
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시 강남구", "02-1234-5678",
				StoreStatus.PENDING);

			given(categoryJpaRepository.existsById(categoryId)).willReturn(true);
			given(storeRepository.save(any(StoreEntity.class))).willReturn(storeEntity);

			StoreResponse response = storeService.createStore(request, userId, false);

			assertThat(response.name()).isEqualTo("치킨집");
			assertThat(response.status()).isEqualTo("PENDING");
			then(storeRepository).should().save(any(StoreEntity.class));
		}

		@Test
		@DisplayName("MANAGER가 가게를 생성하면 APPROVED 상태가 된다")
		void successManager() {
			StoreCreateRequest request = new StoreCreateRequest(categoryId, "치킨집", "서울시 강남구", "02-1234-5678");
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시 강남구", "02-1234-5678",
				StoreStatus.APPROVED);

			given(categoryJpaRepository.existsById(categoryId)).willReturn(true);
			given(storeRepository.save(any(StoreEntity.class))).willReturn(storeEntity);

			StoreResponse response = storeService.createStore(request, userId, true);

			assertThat(response.status()).isEqualTo("APPROVED");
		}

		@Test
		@DisplayName("존재하지 않는 카테고리로 생성하면 예외가 발생한다")
		void failCategoryNotFound() {
			StoreCreateRequest request = new StoreCreateRequest(categoryId, "치킨집", "서울시 강남구", "02-1234-5678");

			given(categoryJpaRepository.existsById(categoryId)).willReturn(false);

			assertThatThrownBy(() -> storeService.createStore(request, userId, false))
				.isInstanceOf(StoreException.class)
				.satisfies(e -> {
					StoreException se = (StoreException)e;
					assertThat(se.getErrorCode()).isEqualTo(StoreErrorCode.CATEGORY_NOT_FOUND);
				});
		}
	}

	@Nested
	@DisplayName("가게 단건 조회")
	class GetStore {

		@Test
		@DisplayName("가게를 정상적으로 조회한다")
		void success() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시 강남구", "02-1234-5678",
				StoreStatus.APPROVED);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			StoreDetailResponse response = storeService.getStore(storeId);

			assertThat(response.name()).isEqualTo("치킨집");
			assertThat(response.address()).isEqualTo("서울시 강남구");
		}

		@Test
		@DisplayName("존재하지 않는 가게를 조회하면 예외가 발생한다")
		void failNotFound() {
			UUID storeId = UUID.randomUUID();
			given(storeRepository.findById(storeId)).willReturn(Optional.empty());

			assertThatThrownBy(() -> storeService.getStore(storeId))
				.isInstanceOf(StoreException.class)
				.satisfies(e -> {
					StoreException se = (StoreException)e;
					assertThat(se.getErrorCode()).isEqualTo(StoreErrorCode.STORE_NOT_FOUND);
				});
		}
	}

	@Nested
	@DisplayName("가게 검색")
	class SearchStores {

		@Test
		@DisplayName("키워드로 가게를 검색한다")
		void success() {
			Pageable pageable = PageRequest.of(0, 10);
			StoreEntity store1 = StoreEntity.create(userId, categoryId, "맛있는 치킨", "서울시", null, StoreStatus.APPROVED);

			given(storeRepository.searchByNameAndCategory("치킨", null, pageable))
				.willReturn(new SliceImpl<>(List.of(store1), pageable, false));

			PageResponse<StoreSearchResponse> response = storeService.searchStores("치킨", null, pageable);

			assertThat(response.contents()).hasSize(1);
			assertThat(response.contents().get(0).name()).isEqualTo("맛있는 치킨");
		}
	}

	@Nested
	@DisplayName("가게 상태 변경")
	class UpdateStoreStatus {

		@Test
		@DisplayName("PENDING 가게를 승인한다")
		void successApprove() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.PENDING);
			StoreStatusUpdateRequest request = new StoreStatusUpdateRequest("APPROVED", null, null);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			StoreResponse response = storeService.updateStoreStatus(storeId, request);

			assertThat(response.status()).isEqualTo("APPROVED");
		}

		@Test
		@DisplayName("PENDING 가게를 거부한다")
		void successReject() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.PENDING);
			StoreStatusUpdateRequest request = new StoreStatusUpdateRequest("REJECTED", "POLICY", "정책 위반");

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			StoreResponse response = storeService.updateStoreStatus(storeId, request);

			assertThat(response.status()).isEqualTo("REJECTED");
		}

		@Test
		@DisplayName("이미 승인된 가게의 상태를 변경하면 예외가 발생한다")
		void failAlreadyApproved() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);
			StoreStatusUpdateRequest request = new StoreStatusUpdateRequest("REJECTED", null, null);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			assertThatThrownBy(() -> storeService.updateStoreStatus(storeId, request))
				.isInstanceOf(StoreException.class)
				.satisfies(e -> {
					StoreException se = (StoreException)e;
					assertThat(se.getErrorCode()).isEqualTo(StoreErrorCode.INVALID_STATUS_CHANGE);
				});
		}
	}

	@Nested
	@DisplayName("가게 수정")
	class UpdateStore {

		@Test
		@DisplayName("소유자가 본인 가게를 수정한다")
		void successOwner() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);
			StoreUpdateRequest request = new StoreUpdateRequest(categoryId, "새치킨집", "부산시", "051-1234-5678");

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));
			given(categoryJpaRepository.existsById(categoryId)).willReturn(true);

			StoreResponse response = storeService.updateStore(storeId, request, userId, false);

			assertThat(response.name()).isEqualTo("새치킨집");
			assertThat(response.address()).isEqualTo("부산시");
		}

		@Test
		@DisplayName("소유자가 아닌 OWNER가 수정하면 예외가 발생한다")
		void failNotOwner() {
			UUID storeId = UUID.randomUUID();
			UUID otherUserId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);
			StoreUpdateRequest request = new StoreUpdateRequest(categoryId, "새치킨집", "부산시", null);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			assertThatThrownBy(() -> storeService.updateStore(storeId, request, otherUserId, false))
				.isInstanceOf(StoreException.class)
				.satisfies(e -> {
					StoreException se = (StoreException)e;
					assertThat(se.getErrorCode()).isEqualTo(StoreErrorCode.NOT_STORE_OWNER);
				});
		}

		@Test
		@DisplayName("MANAGER는 다른 사람의 가게도 수정할 수 있다")
		void successManager() {
			UUID storeId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);
			StoreUpdateRequest request = new StoreUpdateRequest(categoryId, "새치킨집", "부산시", null);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));
			given(categoryJpaRepository.existsById(categoryId)).willReturn(true);

			StoreResponse response = storeService.updateStore(storeId, request, managerId, true);

			assertThat(response.name()).isEqualTo("새치킨집");
		}
	}

	@Nested
	@DisplayName("가게 삭제")
	class DeleteStore {

		@Test
		@DisplayName("소유자가 본인 가게를 삭제한다")
		void successOwner() {
			UUID storeId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			storeService.deleteStore(storeId, userId, false);

			assertThat(storeEntity.isDeleted()).isTrue();
		}

		@Test
		@DisplayName("소유자가 아닌 사용자가 삭제하면 예외가 발생한다")
		void failNotOwner() {
			UUID storeId = UUID.randomUUID();
			UUID otherUserId = UUID.randomUUID();
			StoreEntity storeEntity = StoreEntity.create(userId, categoryId, "치킨집", "서울시", null,
				StoreStatus.APPROVED);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(storeEntity));

			assertThatThrownBy(() -> storeService.deleteStore(storeId, otherUserId, false))
				.isInstanceOf(StoreException.class)
				.satisfies(e -> {
					StoreException se = (StoreException)e;
					assertThat(se.getErrorCode()).isEqualTo(StoreErrorCode.NOT_STORE_OWNER);
				});
		}
	}
}
