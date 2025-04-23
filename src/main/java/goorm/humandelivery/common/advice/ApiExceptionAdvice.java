package goorm.humandelivery.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ApiExceptionAdvice {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> handleNotFountException(Exception e) {
		// sample
		return null;
	}
}
