package com.example.pdelivery.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentProvider {
    TOSS("토스 페이먼츠");

    private final String description;

    PaymentProvider(String description) {
        this.description = description;
    }
}
