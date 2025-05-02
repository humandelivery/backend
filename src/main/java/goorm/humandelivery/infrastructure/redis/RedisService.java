package goorm.humandelivery.infrastructure.redis;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.stereotype.Service;

import goorm.humandelivery.common.exception.CallInfoEntityNotFoundException;
import goorm.humandelivery.domain.model.entity.CallStatus;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisService {

	private final StringRedisTemplate redisTemplate;

	@Autowired
	public RedisService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public String getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public String getLastUpdate(String reservedDriver) {
		String key = RedisKeyParser.taxiDriverLastUpdate(reservedDriver);
		return redisTemplate.opsForValue().get(key);
	}

	public void setValueWithTTL(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	public void setValueWithTTLIfAbsent(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
	}

	public void setLocation(String key, String loginId, Location location) {
		redisTemplate.opsForGeo().add(key, new Point(location.getLongitude(), location.getLatitude()), loginId);
	}

	public void setCallWith(Long callId, CallStatus callStatus) {
		String key = RedisKeyParser.callStatus(callId);
		redisTemplate.opsForValue().set(key, callStatus.name(), Duration.ofHours(1));
	}

	public CallStatus getCallStatus(Long callId) {
		String statusStr = redisTemplate.opsForValue().get(RedisKeyParser.callStatus(callId));
		return statusStr == null ? CallStatus.DONE : CallStatus.valueOf(statusStr);
	}

	public void setDriversStatus(String taxiDriverLoginId, TaxiDriverStatus status) {
		String statusKey = RedisKeyParser.taxiDriverStatus(taxiDriverLoginId);
		setValueWithTTL(statusKey, status.name(), Duration.ofHours(1));
	}

	public TaxiDriverStatus getDriverStatus(String driverId) {
		String key = RedisKeyParser.taxiDriverStatus(driverId);
		String statusStr = redisTemplate.opsForValue().get(key);

		if (statusStr == null) {
			return TaxiDriverStatus.OFF_DUTY;
		}
		return TaxiDriverStatus.valueOf(statusStr);
	}

	public void setActive(String taxiDriverLoginId) {
		redisTemplate.opsForSet().add(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY, taxiDriverLoginId);
	}

	public Set<String> getActiveDrivers() {
		Set<String> members = redisTemplate.opsForSet().members(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY);
		return members == null ? Set.of() : members;
	}

	public void setOffDuty(String taxiDriverLoginId) {
		redisTemplate.opsForSet().remove(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY, taxiDriverLoginId);
	}

	public void setDriversTaxiType(String taxiDriverLoginId, TaxiType taxiType) {
		String taxiTypeKey = RedisKeyParser.taxiDriversTaxiType(taxiDriverLoginId);
		setValueWithTTLIfAbsent(taxiTypeKey, taxiType.name(), Duration.ofDays(1));
	}

	public TaxiType getDriversTaxiType(String taxiDriverLoginId) {
		String taxiTypeKey = RedisKeyParser.taxiDriversTaxiType(taxiDriverLoginId);
		return TaxiType.valueOf(redisTemplate.opsForValue().get(taxiTypeKey));
	}

	public boolean tryAcceptCall(String callId, String taxiDriverLoginId) {
		// Nullable Boolean 반환. 아래와 같이 NullPointerException 처리.
		return Boolean.TRUE.equals(
			redisTemplate.opsForValue().setIfAbsent(callId, taxiDriverLoginId, Duration.ofMinutes(5))
		);
	}

	public void removeFromLocation(String taxiDriverLoginId, TaxiType taxiType, TaxiDriverStatus driverStatus) {
		String key = RedisKeyParser.getTaxiDriverLocationKeyBy(driverStatus, taxiType);
		redisTemplate.opsForZSet().remove(key, taxiDriverLoginId);
	}

	public void addRejectedDriverToCall(Long callId, String taxiDriverLoginId) {
		String key = RedisKeyParser.getRejectCallKey(callId);
		// 운행 완료 후에 해당 set 딜리트 필요.
		redisTemplate.opsForSet().add(key, taxiDriverLoginId);
	}

	public boolean isDriverRejectedForCall(Long callId, String taxiDriverLoginId) {
		String key = RedisKeyParser.getRejectCallKey(callId);
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, taxiDriverLoginId));
	}

	public List<String> findNearByAvailableDrivers(Long callId, TaxiType taxiType, double latitude, double longitude,
		double radiusInKm) {

		String key = RedisKeyParser.getTaxiDriverLocationKeyBy(TaxiDriverStatus.AVAILABLE, taxiType);

		GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

		// new Point 내부 인자는 경도 / 위도 순서로 넣습니다
		GeoResults<RedisGeoCommands.GeoLocation<String>> results =
			geoOps.radius(key, new Circle(new Point(longitude, latitude), new Distance(radiusInKm, Metrics.KILOMETERS))
			);

		if (results == null) {
			return List.of();
		}

		List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = results.getContent();

		List<String> driversIds = content.stream()
			.map(GeoResult::getContent)
			.map(GeoLocation::getName)
			.toList();

		/**
		 * 여기서 이미 해당 콜을 거절한 기사인지 체크하고, 거절하지 않은 기사들에게만 콜 요청을 보냅니다.
		 */
		return driversIds.stream().filter(id -> !isDriverRejectedForCall(callId, id)).toList();
	}

	public void assignCallToDriver(Long callId, String taxiDriverLoginId) {
		String key = RedisKeyParser.assignCallToDriver(taxiDriverLoginId);

		// taxidriver:기사아이디:call, callId
		redisTemplate.opsForValue().set(key, String.valueOf(callId));
	}

	public Long getCallIdByDriverId(String taxiDriverLoginId) {
		String key = RedisKeyParser.assignCallToDriver(taxiDriverLoginId);
		String callIdStr = redisTemplate.opsForValue().get(key);

		if (callIdStr == null) {
			log.info("[getCallIdByDriverId.RedisService] redis 에 해당 키가 존재하지 않음. key : {}", key);
			throw new CallInfoEntityNotFoundException();
		}

		return Long.valueOf(callIdStr);
	}

	public void deleteCallStatus(Long callId) {
		String key = RedisKeyParser.callStatus(callId);
		redisTemplate.delete(key);
	}

	public void deleteAssignedCallOf(String driverLoginId) {
		String key = RedisKeyParser.assignCallToDriver(driverLoginId);
		redisTemplate.delete(key);
	}

	public void removeRejectedDriversForCall(Long callId) {
		String key = RedisKeyParser.getRejectCallKey(callId);
		redisTemplate.delete(key);
	}
}
