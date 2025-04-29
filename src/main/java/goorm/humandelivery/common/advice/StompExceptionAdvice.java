package goorm.humandelivery.common.advice;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import goorm.humandelivery.common.exception.CustomerNotAssignedException;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
import goorm.humandelivery.domain.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class StompExceptionAdvice {

	@MessageExceptionHandler(Exception.class)
	@SendToUser("/queue/errors")
	public ErrorResponse handle(Exception e) {
		log.error("WebSocket 처리 중 예외 발생", e);
		return new ErrorResponse("ERROR", e.getMessage());
	}

	@MessageExceptionHandler(OffDutyLocationUpdateException.class)
	@SendToUser("/queue/errors") // /user/queue/errors
	public ErrorResponse handleOffDutyLocationUpdateException(OffDutyLocationUpdateException ex) {
		log.error("exception: {}", ex.getClass());
		return new ErrorResponse("OffDutyLocationUpdateException", ex.getMessage());
	}

	@MessageExceptionHandler(CustomerNotAssignedException.class)
	@SendToUser("/queue/errors") // /user/queue/errors
	public ErrorResponse handleCustomerNotAssignedException(CustomerNotAssignedException ex) {
		log.error("exception: {}", ex.getClass());
		return new ErrorResponse("CustomerNotAssignedException", ex.getMessage());
	}

}
