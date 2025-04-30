package goorm.humandelivery.infrastructure.redis;

import java.time.Duration;
import java.util.List;

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

	public String getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void setValueWithTTL(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	// GEOADD taxidriver:driverId:location 126.9780 37.5665 driverId
	public void setLocation(String key, String loginId, Location location) {
		redisTemplate.opsForGeo().add(key, new Point(location.getLongitude(), location.getLatitude()), loginId);
	}



	public List<String> findNearByDrivers(TaxiType taxiType, double latitude, double longitude, double radiusInKm) {
		String strTaxiType = taxiType.name().toLowerCase();
		log.info("strTaxiType : {} ", strTaxiType);

		String key = RedisKeyParser.taxiDriverLocationKeyFrom(taxiType);

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
}
