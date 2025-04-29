package goorm.humandelivery.infrastructure.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
}
