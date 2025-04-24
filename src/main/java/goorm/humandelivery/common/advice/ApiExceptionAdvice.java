package goorm.humandelivery.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import goorm.humandelivery.common.exception.DuplicateLoginIdException;
import goorm.humandelivery.common.exception.DuplicatePhoneNumberException;
import jakarta.persistence.EntityNotFoundException;
import goorm.humandelivery.domain.model.response.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionAdvice {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> handleNotFountException(Exception e) {
		// sample
		return null;
	}

	@ExceptionHandler(DuplicateLoginIdException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateLoginIdException(DuplicateLoginIdException ex) {
		ErrorResponse response = new ErrorResponse("DUPLICATE_ID", ex.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(DuplicatePhoneNumberException.class)
	public ResponseEntity<ErrorResponse> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException ex) {
		ErrorResponse response = new ErrorResponse("DUPLICATE_PhoneNumber", ex.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.findFirst()
			.orElse("잘못된 입력입니다.");

		ErrorResponse response = new ErrorResponse("VALIDATION_ERROR", errorMessage);
		return ResponseEntity.badRequest().body(response);
	}

}
