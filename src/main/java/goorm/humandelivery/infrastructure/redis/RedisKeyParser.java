package goorm.humandelivery.infrastructure.redis;

public class RedisKeyParser {

	private RedisKeyParser() {
	}

	public static String taxiDriverStatus(String loginId) {
		return String.format("taxidriver:%s:status", loginId);
	}

	public static String taxiDriverLocation(String loginId) {
		return String.format("taxidriver:%s:location", loginId);
	}
}
