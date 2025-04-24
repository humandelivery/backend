package goorm.humandelivery.common.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import goorm.humandelivery.common.exception.IncorrectPasswordException;
import jakarta.persistence.EntityExistsException;

@RestControllerAdvice
public class ApiExceptionAdvice {

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<?> handleEntityExistsException(EntityExistsException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(Map.of(
				"message", e.getMessage()
			));
	}

	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<?> handleIncorrectPasswordException(IncorrectPasswordException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of(
				"message", e.getMessage()
			));
	}
}
