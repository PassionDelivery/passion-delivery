package com.example.pdelivery.shared.jpa;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.pdelivery.shared.error.ErrorCode;
import com.example.pdelivery.shared.error.PDeliveryException;

import jakarta.servlet.http.HttpServletRequest;
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

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {

		String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
			.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
			.collect(Collectors.joining(", "));
		String globalErrors = ex.getBindingResult().getGlobalErrors().stream()
			.map(ge -> ge.getObjectName() + ": " + ge.getDefaultMessage())
			.collect(Collectors.joining(", "));
		String detail = java.util.stream.Stream.of(fieldErrors, globalErrors)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.joining(", "));

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
		problemDetail.setTitle("입력값이 올바르지 않습니다.");
		problemDetail.setProperty("code", "VALIDATION_ERROR");
		problemDetail.setProperty("timestamp", Instant.now());

		return ResponseEntity.badRequest().body(problemDetail);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ProblemDetail> handleAuthorizationDeniedException(AuthorizationDeniedException exception,
		HttpServletRequest request) {
		log.warn("Authorization denied: {}", request.getRequestURI());
		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
		problemDetail.setTitle("접근 권한이 없습니다.");
		problemDetail.setProperty("timestamp", Instant.now());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
	}

	private static ProblemDetail getProblemDetail(ErrorCode errorCode, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status(), exception.getMessage());
		problemDetail.setTitle(errorCode.message());
		problemDetail.setProperty("code", errorCode.code());
		problemDetail.setProperty("timestamp", Instant.now());

		return problemDetail;
	}

}
