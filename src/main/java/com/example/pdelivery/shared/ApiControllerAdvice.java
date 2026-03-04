package com.example.pdelivery.shared;

import java.net.URI;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ProblemDetail handleException(Exception exception) {
		return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
	public ProblemDetail handleIllegalException(Exception exception) {
		return getProblemDetail(HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ProblemDetail> handleResponseStatusException(
			ResponseStatusException ex, HttpServletRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
			ex.getStatusCode(), ex.getReason());
		problem.setInstance(URI.create(request.getRequestURI()));
		return ResponseEntity.status(ex.getStatusCode()).body(problem);
	}

	private static ProblemDetail getProblemDetail(HttpStatus status, Exception exception) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());

		problemDetail.setProperty("timestamp", LocalDateTime.now());
		problemDetail.setProperty("exception", exception.getClass().getSimpleName());

		return problemDetail;
	}
}
