package com.example.pdelivery.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.pdelivery.shared.error.ErrorCode;
import com.example.pdelivery.shared.error.PDeliveryException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(PDeliveryException.class)
	public ResponseEntity<ProblemDetail> handlePDeliveryException(PDeliveryException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		log.warn("PDeliveryException: {} - {}", errorCode.code(), exception.getMessage());
		return ResponseEntity.status(errorCode.status()).body(getProblemDetail(errorCode, exception));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleException(Exception exception) {
		log.error("Unhandled Exception", exception);
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		problemDetail.setTitle("Internal Server Error");
		problemDetail.setDetail("내부 오류가 발생했습니다.");
		problemDetail.setProperty("timestamp", Instant.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
	}

	private static ProblemDetail getProblemDetail(ErrorCode errorCode, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status(), exception.getMessage());
		problemDetail.setTitle(errorCode.message());
		problemDetail.setProperty("code", errorCode.code());
		problemDetail.setProperty("timestamp", Instant.now());

		return problemDetail;
	}

}
