package com.example.pdelivery.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    READY,
    PAID,
    FAILED,
    CANCELLED,
    REFUNDED;

}
