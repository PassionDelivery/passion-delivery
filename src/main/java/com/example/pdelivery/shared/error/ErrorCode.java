package com.example.pdelivery.shared.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String code();
    HttpStatus status();
    String message();
}
