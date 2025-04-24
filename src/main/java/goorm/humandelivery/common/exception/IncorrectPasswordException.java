package goorm.humandelivery.common.exception;

public class IncorrectPasswordException extends RuntimeException{

	public IncorrectPasswordException(String reason) {
		super("패스워드가 일치하지 않습니다.");
	}
}
