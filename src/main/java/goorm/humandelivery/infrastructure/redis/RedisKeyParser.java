package goorm.humandelivery.infrastructure.redis;

import org.apache.kafka.common.protocol.types.Field;

import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;

public class RedisKeyParser {

	public static final String TAXI_DRIVER_LOCATION_KEY = "taxidriver:location";

	private RedisKeyParser() {
	}

	public static String taxiDriverStatus(String loginId) {
		return String.format("taxidriver:%s:status", loginId);
	}

	public static String taxiDriversTaxiType(String loginId) {
		return String.format("taxidriver:%s:type", loginId);
	}

	public static String taxiDriverLocationKeyFrom(TaxiType taxiType) {
		return TAXI_DRIVER_LOCATION_KEY + ":" + taxiType.name().toLowerCase();
	}

	public static String getTaxiDriverLocationKeyBy(TaxiDriverStatus taxiDriverStatus, TaxiType taxiType) {
		return TAXI_DRIVER_LOCATION_KEY + ":" +
			taxiType.name().toLowerCase() + ":" +
			taxiDriverStatus.name().toLowerCase();

	}
}
