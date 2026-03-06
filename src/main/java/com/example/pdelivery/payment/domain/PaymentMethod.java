package com.example.pdelivery.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    CARD("카드결제"),
    EASY_PAY("간편 결제"),
    TRANSFER("계좌 이체");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}
