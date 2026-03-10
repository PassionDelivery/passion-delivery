package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.application.OrderRequest.*;
import static com.example.pdelivery.order.domain.Order.*;
import static com.example.pdelivery.order.error.OrderErrorCode.*;
import static com.example.pdelivery.payment.domain.PaymentMethod.*;
import static com.example.pdelivery.shared.enums.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentRepository;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
public class OrderServiceIntegrationTest {
	@Autowired
	OrderService orderService;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	EntityManager em;

	private UUID storeId = UUID.randomUUID();
	private UUID customerId;

	@Nested
	@Transactional
	@DisplayName("주문 상태 변경 테스트")
	class OrderStatusServiceTest {
		Order order1;
		Order order2;

		@BeforeEach
		void setUpOrderStatus() {
			UserEntity user = UserEntity.create("abcd", "abcdQW12!@", "abcd", "abcd@gmail.com", UserRole.CUSTOMER);
			userRepository.save(user);
			customerId = user.getId();
			UUID chicken = UUID.randomUUID();
			UUID pizza = UUID.randomUUID();
			String address = "서울시 종로구 12-54";
			List<OrderLineVO> orderLineVOs1 = List.of(
				new OrderLineVO(pizza, "pizza", 2, 17000),
				new OrderLineVO(chicken, "chicken", 1, 20000)
			);
			List<OrderLineVO> orderLineVOs2 = List.of(
				new OrderLineVO(chicken, "chicken", 2, 20000)
			);

			order1 = create(storeId, address, customerId, orderLineVOs1);
			order2 = create(storeId, address, customerId, orderLineVOs2);

			orderRepository.save(order1);
			orderRepository.save(order2);
		}

		@Test
		@DisplayName("주문 취소 성공 test")
		public void cancelOrder_Success() {
			OrderCancelRequest req = new OrderCancelRequest("단순 변심");

			Payment payment = Payment.create(order1.getId(),
				order1.getStoreId(),
				PaymentProvider.TOSS,
				CARD,
				order1.getTotalPrice());
			paymentRepository.save(payment);

			assertThat(order1.checkCancellation()).isFalse();
			orderService.cancelOrder(customerId, order1.getId(), req);

			em.flush();
			em.clear();

			Order findResult = orderRepository.findById(order1.getId()).get();
			assertThat(findResult.checkCancellation()).isTrue();
		}

		@Test
		@DisplayName("주문 취소 성공")
		public void cancelOrder_TimeoutBoundary() {
			OrderCancelRequest req = new OrderCancelRequest("단순 변심");

			Payment payment = Payment.create(order1.getId(),
				order1.getStoreId(),
				PaymentProvider.TOSS,
				CARD,
				order1.getTotalPrice());
			paymentRepository.save(payment);

			ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusMinutes(4).minusSeconds(59));
			orderRepository.save(order1);
			em.flush();
			em.clear();

			assertThat(order1.checkCancellation()).isFalse();
			orderService.cancelOrder(customerId, order1.getId(), req);

			em.flush();
			em.clear();

			Order findResult = orderRepository.findById(order1.getId()).get();
			assertThat(findResult.checkCancellation()).isTrue();
		}

		@Test
		@DisplayName("주문 취소 실패 - 시간초과")
		public void cancelOrder_Timeout() {
			OrderCancelRequest req = new OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusMinutes(5));
			orderRepository.save(order1);

			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.cancelOrder(customerId, order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.CANCEL_TIMEOUT);
		}

		@Test
		@DisplayName("주문 취소 실패 - 중복 취소")
		public void cancelOrder_AlreadyCancel() {
			OrderCancelRequest req = new OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "status", CANCELLED);
			orderRepository.save(order1);
			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.cancelOrder(customerId, order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.ALREADY_CANCELED);
		}

		@Test
		@DisplayName("주문 취소 실패 - 이미 수락 상태")
		public void cancelOrder_AlreadyAccepted() {
			OrderCancelRequest req = new OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "status", ACCEPTED);
			orderRepository.save(order1);
			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.cancelOrder(customerId, order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.INVALID_CANCEL_STATUS);
		}

		@Test
		@DisplayName("주문 상태 변경 - 성공")
		public void changeOrderStatus_Success() {
			OrderChangeStatusRequest req = new OrderChangeStatusRequest(ACCEPTED, null);
			UUID orderId = order2.getId();

			Order beforeOrder = orderRepository.findById(orderId).orElseThrow();
			OrderView beforeOrderView = new OrderView(beforeOrder);
			assertThat(beforeOrderView.getStatus()).isNotEqualTo(ACCEPTED);

			orderService.changeStatusOrder(orderId, req);
			em.flush();
			em.clear();

			Order afterOrder = orderRepository.findById(orderId).orElseThrow();
			OrderView afterOrderView = new OrderView(afterOrder);
			assertThat(afterOrderView.getStatus()).isEqualTo(ACCEPTED);
		}

		@Test
		@DisplayName("주문 거절 성공")
		public void changeOrderStatus_Rejected() {
			OrderChangeStatusRequest req = new OrderChangeStatusRequest(REJECTED, "재료 소진");
			UUID orderId = order2.getId();

			Order beforeOrder = orderRepository.findById(orderId).orElseThrow();
			OrderView beforeOrderView = new OrderView(beforeOrder);
			assertThat(beforeOrderView.getStatus()).isNotEqualTo(REJECTED);

			orderService.changeStatusOrder(orderId, req);
			em.flush();
			em.clear();

			Order afterOrder = orderRepository.findById(orderId).orElseThrow();
			OrderView afterOrderView = new OrderView(afterOrder);
			assertThat(afterOrderView.getStatus()).isEqualTo(REJECTED);
		}

		@Test
		@DisplayName("주문 실패 - 이미 취소된 주문")
		public void changeOrderStatus_CancelledStatus() {
			OrderChangeStatusRequest req = new OrderChangeStatusRequest(REJECTED, "재료 소진");
			UUID orderId = order2.getId();
			ReflectionTestUtils.setField(order2, "status", CANCELLED);
			orderRepository.save(order2);
			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.changeStatusOrder(orderId, req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.ALREADY_CANCELED);
		}

		@Test
		@DisplayName("주문 실패 - 이미 완료된 주문")
		public void changeOrderStatus_CompletedStatus() {
			OrderChangeStatusRequest req = new OrderChangeStatusRequest(COOKED, null);
			UUID orderId = order2.getId();
			ReflectionTestUtils.setField(order2, "status", COMPLETED);
			orderRepository.save(order2);
			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.changeStatusOrder(orderId, req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.ALREADY_ORDER_COMPLETED);
		}

		@Test
		@DisplayName("주문 실패 - PENDING 상태 아닌 주문")
		public void changeOrderStatus_NoPendingStatus() {
			OrderChangeStatusRequest req = new OrderChangeStatusRequest(REJECTED, "재료소진");
			UUID orderId = order2.getId();
			ReflectionTestUtils.setField(order2, "status", ACCEPTED);
			orderRepository.save(order2);
			em.flush();
			em.clear();

			assertThatThrownBy(() -> orderService.changeStatusOrder(orderId, req))
				// .isInstanceOf(OrderException.class)
				.asInstanceOf(type(OrderException.class)) // AssertJ의 검증 대상 타입이 OrderException 바꿈
				.returns(INVALID_CHANGE_STATUS, from(OrderException::getErrorCode))
				.returns("reject는 PENDING 상태에서만 가능합니다.", from(OrderException::getMessage));
		}
	}

	@Nested
	@Transactional
	@DisplayName("주문 삭제 테스트")
	class OrderDeleteTest {
		Order order;

		@Test
		@DisplayName("주문 삭제 test")
		void deleteTest() {
			UUID chicken = UUID.randomUUID();
			UUID pizza = UUID.randomUUID();
			UUID customerId = UUID.randomUUID();
			String address = "서울시 종로구 12-54";
			List<OrderLineVO> orderLineVOs = List.of(
				new OrderLineVO(pizza, "pizza", 2, 17000),
				new OrderLineVO(chicken, "chicken", 1, 20000)
			);
			order = create(storeId, address, customerId, orderLineVOs);
			orderRepository.save(order);

			assertThat(order.getDeletedAt()).isNull();
			orderService.deleteOrder(customerId, order.getId());
			em.flush();
			em.clear();

			Order deletedOrder = orderRepository.findById(order.getId()).orElseThrow();
			assertThat(deletedOrder.getDeletedAt()).isNotNull();
		}
	}
}
