package goorm.humandelivery.common.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.security.SignatureException;


import goorm.humandelivery.common.exception.IncorrectPasswordException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ApiExceptionAdvice {

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<?> handleEntityExists(EntityExistsException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(Map.of(
				"message", e.getMessage()
			));
	}

	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<?> handleIncorrectPassword(IncorrectPasswordException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of(
				"message", e.getMessage()
			));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(Map.of(
				"message", e.getMessage()
			));
	}

	/**
	 * GET : /api/v1/taxi-driver/token-info
	 * 요청 시 헤더 누락의 경우 발생하는 예외
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<?> handleMissingRequestHeader(MissingRequestHeaderException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of(
				"message", "헤더 정보를 포함해주세요."
			));
	}

	/**
	 * GET : /api/v1/taxi-driver/token-info
	 * 토큰 서명 실패 시 발생하는 예외
	 */
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<?> handleSignatureException(SignatureException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of(
				"message", "올바른 토큰이 아닙니다."
			));
	}
}
