package com.example.pdelivery.payment.application.dto;

import java.util.UUID;

import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequest(@NotNull(message = "주문 ID는 필수입니다.") UUID orderId,
								   @NotNull(message = "가게 ID는 필수입니다.") UUID storeId,
								   @NotNull(message = "결제수단은 필수 입니다.") PaymentMethod paymentMethod,
								   @NotNull(message = "결제사는 필수입니다.") PaymentProvider paymentProvider,
								   @NotNull(message = "결제 금액은 필수입니다.") @Positive(message = "결제 금액은 0 보다 커야 합니다.") Long amount

) {

	public CreatePaymentRequest {
		if (orderId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_ORDER_ID);
		}
		if (storeId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_STORE_ID);
		}
		if (amount == null || amount <= 0) {
			throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
		}
		if (paymentMethod == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
		}
		if (paymentProvider == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_PROVIDER);
		}
	}
}
