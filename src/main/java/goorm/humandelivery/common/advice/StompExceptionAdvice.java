package goorm.humandelivery.common.advice;

import java.io.IOException;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import goorm.humandelivery.domain.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class StompExceptionAdvice {

	@MessageExceptionHandler(IOException.class)
	@SendToUser("/queue/errors") // /user/queue/errors
	public ErrorResponse handleException(IOException exception) {
		log.error("exception: {}", exception.getClass());
		return new ErrorResponse("IO_ERROR", "입출력 오류가 발생했습니다.");
	}
}
