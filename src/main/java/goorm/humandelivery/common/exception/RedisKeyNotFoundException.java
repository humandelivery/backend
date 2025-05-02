package goorm.humandelivery.common.exception;

public class RedisKeyNotFoundException extends RuntimeException {
	public RedisKeyNotFoundException(String key) {
		super(String.format("해당 키가 Redis 에 존재하지 않습니다. key : %s", key));
	}
}
