package goorm.humandelivery.infrastructure.redis;

public class RedisKeyParser {

	private static final String TAXI_DRIVER_LOCATION_KEY = "taxidriver:location";

	private RedisKeyParser() {
	}

	public static String taxiDriverStatus(String loginId) {
		return String.format("taxidriver:%s:status", loginId);
	}

	public static String taxiDriverLocation() {
		return TAXI_DRIVER_LOCATION_KEY;
	}
}
