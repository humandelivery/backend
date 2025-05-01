package goorm.humandelivery.application;

import static goorm.humandelivery.domain.model.entity.TaxiDriverStatus.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import goorm.humandelivery.infrastructure.redis.RedisService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaxiDriverConnectionMonitor {

	private final RedisService redisService;
	private static final long TIMEOUT_MILLIS = 10_000;

	@Autowired
	public TaxiDriverConnectionMonitor(RedisService redisService) {
		this.redisService = redisService;
	}

	/**
	 * Scheduled 규칙
	 *    Method 는 void 타입으로
	 *    Method 는 매개변수 사용 불가
	 *    이전 작업 종류 이후 시점으로부터 정의된 시간만큼 지난 후 Task를 실행한다.
	 */

	// 5초에 한번씩 스케쥴링
	@Scheduled(fixedDelay = 5000)
	public void monitorReservedTaxiDrivers() {
		long now = System.currentTimeMillis();

		log.info("[monitorReservedTaxiDrivers.TaxiDriverConnectionMonitor] start monitoring at : {}",
			Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDateTime()
		);

		// 1. 운행중인 기사목록 조회
		Set<String> activeDrivers = redisService.getActiveDrivers();

		// 2. 기사들 중 RESERVED 상태인 기사만 조회
		List<String> reservedDrivers = activeDrivers.stream()
			.filter(driverId -> redisService.getDriverStatus(driverId) == RESERVED)
			.toList();

		// 3. reservedDrivers 의 마지막 위치정보 시간 조회
		for (String reservedDriver : reservedDrivers) {
			String lastUpdateStr = redisService.getLastUpdate(reservedDriver);

			if (lastUpdateStr == null) {
				/**
				 * TODO : 배차 취소 로직 구현 콜, 매칭, 운행정보 엔티티 삭제 및 택시 상태 변경, Redis 에서 관련 데이터 제거 필요.
				 */

				// 로직

				/**
				 * TODO : 이후 고객에게 예외응답 전송, 택시에게 예외응답 전송
				 */
				continue;
			}

			long lastUpdateTime = Long.parseLong(lastUpdateStr);
			if (now - lastUpdateTime > TIMEOUT_MILLIS) {
				log.warn("[{}] 위치 미갱신 ({}ms 지남)", reservedDriver, now - lastUpdateTime);
				/**
				 * TODO : 배차 취소 로직 구현 콜, 매칭, 운행정보 엔티티 삭제 및 택시 상태 변경, Redis 에서 관련 데이터 제거 필요.
				 */
				// 로직
				/**
				 * TODO : 이후 고객에게 예외응답 전송, 택시에게 예외응답 전송
				 */
			}

		}

	}

}
