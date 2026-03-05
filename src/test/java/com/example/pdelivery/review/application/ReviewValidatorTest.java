package com.example.pdelivery.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pdelivery.order.domain.OrderStatus;
import com.example.pdelivery.review.ReviewErrorCode;
import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.review.infrastructure.OrderData;
import com.example.pdelivery.review.infrastructure.ReviewOrderRequirer;
import com.example.pdelivery.review.infrastructure.ReviewStoreRequirer;
import com.example.pdelivery.review.infrastructure.ReviewUserRequirer;

@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 등록 시 검증 테스트")
class ReviewValidatorTest {
	@InjectMocks
	ReviewValidator reviewValidator;

	@Mock
	ReviewUserRequirer reviewUserRequirer;

	@Mock
	ReviewStoreRequirer reviewStoreRequirer;

	@Mock
	ReviewOrderRequirer reviewOrderRequirer;


	@Test
	@DisplayName("리뷰 검증 성공")
	void validate() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);
		var orderData = new OrderData(OrderStatus.COMPLETED);

		given(reviewUserRequirer.existsBy(any())).willReturn(true);
		given(reviewStoreRequirer.existsBy(any())).willReturn(true);
		given(reviewOrderRequirer.getOrderInfo(any())).willReturn(orderData);

		assertThatCode(() -> reviewValidator.validate(customerId, reviewRequest))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("유저가 존재하지 않는 경우")
	void validateFail1() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);

		given(reviewUserRequirer.existsBy(any())).willReturn(false);

		var message = assertThatThrownBy(
			() -> reviewValidator.validate(customerId, reviewRequest))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message).isEqualTo(ReviewErrorCode.REVIEW_USER_NOT_FOUND.message());
	}

	@Test
	@DisplayName("가게가 존재하지 않는 경우")
	void validateFail2() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);

		given(reviewUserRequirer.existsBy(any())).willReturn(true);
		given(reviewStoreRequirer.existsBy(any())).willReturn(false);

		var message = assertThatThrownBy(
			() -> reviewValidator.validate(customerId, reviewRequest))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message).isEqualTo(ReviewErrorCode.REVIEW_STORE_NOT_FOUND.message());
	}

	@Test
	@DisplayName("주문이 존재하지 않는 경우")
		//Todo: OrderProvider 연동 후 테스트
	void validateFail3() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);

		given(reviewUserRequirer.existsBy(any())).willReturn(true);
		given(reviewStoreRequirer.existsBy(any())).willReturn(true);
		given(reviewOrderRequirer.getOrderInfo(any()))
			.willThrow(new ReviewException(ReviewErrorCode.REVIEW_ORDER_NOT_FOUND));

		var message = assertThatThrownBy(
			() -> reviewValidator.validate(customerId, reviewRequest))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message).isEqualTo(ReviewErrorCode.REVIEW_ORDER_NOT_FOUND.message());
	}

	@Test
	@DisplayName("주문의 상태가 완료가 아닌 경우")
	void validateFail4() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);
		var orderData = new OrderData(OrderStatus.REJECTED);

		given(reviewUserRequirer.existsBy(any())).willReturn(true);
		given(reviewStoreRequirer.existsBy(any())).willReturn(true);
		given(reviewOrderRequirer.getOrderInfo(any())).willReturn(orderData);

		var message = assertThatThrownBy(
			() -> reviewValidator.validate(customerId, reviewRequest))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message).isEqualTo(ReviewErrorCode.REVIEW_ORDER_INVALID_STATUS.message());
	}
}