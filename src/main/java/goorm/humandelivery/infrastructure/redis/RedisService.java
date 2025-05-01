package goorm.humandelivery.infrastructure.redis;

import java.time.Duration;
import java.util.Collections;
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

	public void setValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public boolean setValueIfAbsent(String key, String value) {
		return redisTemplate.opsForValue().setIfAbsent(key, value);
	}

	public String getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void setValueWithTTL(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	public void setValueWithTTLIfAbsent(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
	}

	// GEOADD taxidriver:driverId:location 126.9780 37.5665 driverId
	public void setLocation(String key, String loginId, Location location) {
		redisTemplate.opsForGeo().add(key, new Point(location.getLongitude(), location.getLatitude()), loginId);
	}


	// 택시 타입별, 출발지로부터 인근 택시기사 조회
	// 택시가 많아지면 느려진다!
	// 조건들이 추가됐을 때 조회가 가능할지......
	public List<String> findNearByAvailableDrivers(TaxiType taxiType, double latitude, double longitude, double radiusInKm) {

		// String key = RedisKeyParser.taxiDriverLocationKeyFrom(taxiType);
		String key = RedisKeyParser.getTaxiDriverLocationKeyBy(TaxiDriverStatus.AVAILABLE, taxiType);


		GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

		// new Point 내부 인자는 경도 / 위도 순서로 넣습니다
		GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(
			key,
			new Circle(new Point(longitude, latitude), new Distance(radiusInKm, Metrics.KILOMETERS))
		);

		return results.getContent().stream()
			.map(GeoResult::getContent)
			.map(GeoLocation::getName)
			.toList();
	}

	public void setDriversStatus(String taxiDriverLoginId, TaxiDriverStatus status) {
		String statusKey = RedisKeyParser.taxiDriverStatus(taxiDriverLoginId);
		setValueWithTTL(statusKey, status.name(), Duration.ofHours(1));

	}

	public void setDriversTaxiType(String taxiDriverLoginId, TaxiType taxiType) {
		String taxiTypeKey = RedisKeyParser.taxiDriversTaxiType(taxiDriverLoginId);
		setValueWithTTLIfAbsent(taxiTypeKey, taxiType.name(), Duration.ofDays(1));
	}

	public void setActive(String taxiDriverLoginId) {

		log.info("setactive 실행 ");
		redisTemplate.opsForSet().add(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY, taxiDriverLoginId);
	}

	public void setOffDuty(String taxiDriverLoginId) {
		redisTemplate.opsForSet().remove(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY, taxiDriverLoginId);
	}

	public Set<String> getActiveDrivers() {
		return redisTemplate.opsForSet().members(RedisKeyParser.ACTIVE_TAXI_DRIVER_KEY);

	}

	public TaxiDriverStatus getDriverStatus(String taxiDriverLoginId) {
		String key = RedisKeyParser.taxiDriverStatus(taxiDriverLoginId);
		return 	TaxiDriverStatus.valueOf(redisTemplate.opsForValue().get(key));

	}

	public String getLastUpdate(String reservedDriver) {
		String key = RedisKeyParser.taxiDriverLastUpdate(reservedDriver);
		return redisTemplate.opsForValue().get(key);
	}
}
