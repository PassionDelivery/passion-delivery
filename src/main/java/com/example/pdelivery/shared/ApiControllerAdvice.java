package com.example.pdelivery.shared;

import java.time.Instant;
import java.time.LocalDateTime;

import com.example.pdelivery.shared.error.ErrorCode;
import com.example.pdelivery.shared.error.PDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
	public ProblemDetail handleException(Exception exception) {
		return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
	public ProblemDetail handleIllegalException(Exception exception) {
		return getProblemDetail(HttpStatus.BAD_REQUEST, exception);
	}

	private static ProblemDetail getProblemDetail(ErrorCode errorCode, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status(), exception.getMessage());
		problemDetail.setTitle(errorCode.message());
		problemDetail.setProperty("code", errorCode.code());
		problemDetail.setProperty("timestamp", Instant.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());
		return problemDetail;
	}

	private static ProblemDetail getProblemDetail(HttpStatus status, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());

		problemDetail.setProperty("timestamp", LocalDateTime.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());

		return problemDetail;
	}
}
