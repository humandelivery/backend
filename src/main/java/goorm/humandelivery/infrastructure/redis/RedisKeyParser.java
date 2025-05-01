package goorm.humandelivery.infrastructure.redis;

import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;

public class RedisKeyParser {

	public static final String TAXI_DRIVER_LOCATION_KEY = "taxidriver:location";
	public static final String ACTIVE_TAXI_DRIVER_KEY = "taxidriver:active";

	private RedisKeyParser() {
	}

	public static String taxiDriverStatus(String taxiDriverLoginId) {
		return String.format("taxidriver:%s:status", taxiDriverLoginId);
	}

	public static String taxiDriversTaxiType(String taxiDriverLoginId) {
		return String.format("taxidriver:%s:type", taxiDriverLoginId);
	}

	public static String taxiDriverLastUpdate(String taxiDriverLoginId) {
		return String.format("taxidriver:%s:lastupdate", taxiDriverLoginId);
	}


	public static String getTaxiDriverLocationKeyBy(TaxiDriverStatus taxiDriverStatus, TaxiType taxiType) {
		return TAXI_DRIVER_LOCATION_KEY + ":" +
			taxiType.name().toLowerCase() + ":" +
			taxiDriverStatus.name().toLowerCase();

	}

	public static String callStatus(Long callId) {
		return String.format("call:%s:status", callId);
	}

	public static String getRejectCallKey(Long callId) {
		return String.format("call:%s:rejected", callId);
	}
}
