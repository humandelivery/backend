package goorm.humandelivery.infrastructure.messaging;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import goorm.humandelivery.common.exception.CustomerNotAssignedException;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.request.LocationResponse;
import goorm.humandelivery.domain.model.response.DrivingInfoResponse;
import goorm.humandelivery.domain.model.response.DrivingSummaryResponse;
import goorm.humandelivery.domain.model.response.ErrorResponse;
import goorm.humandelivery.domain.model.response.MatchingSuccessResponse;
import goorm.humandelivery.infrastructure.redis.RedisKeyParser;
import goorm.humandelivery.infrastructure.redis.RedisService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessagingService {

	private static final String LOCATION_TO_USER = "/queue/update-taxidriver-location";
	private static final String RIDE_STATUS_TO_USER = "/queue/ride-status";
	private static final String DISPATCH_DRIVING_STATUS_MESSAGE = "/queue/ride-status";
	private static final String DISPATCH_DRIVING_RESULT_MESSAGE = "/queue/driving-result";
	private static final String DISPATCH_FAIL_MESSAGE_TO_USER = "/queue/dispatch-error";
	private static final String DISPATCH_FAIL_MESSAGE_TO_TAXI_DRIVER = "/queue/dispatch-canceled";

	private final SimpMessagingTemplate messagingTemplate;
	private final RedisService redisService;

	@Autowired
	public MessagingService(SimpMessagingTemplate messagingTemplate, RedisService redisService) {
		this.messagingTemplate = messagingTemplate;
		this.redisService = redisService;
	}

	public void sendLocation(String taxiDriverLoginId, TaxiDriverStatus status, TaxiType taxiType,
		String customerLoginId, Location location
	) {
		log.info("[MessagingService sendMessage : 호출] 택시기사아이디 : {}, 택시기사상태 : {}, 택시타입 : {},  고객아이디 : {} ",
			taxiDriverLoginId, status, taxiType, customerLoginId);

		LocationResponse response = new LocationResponse(location);

		if (status == TaxiDriverStatus.OFF_DUTY) {
			throw new OffDutyLocationUpdateException();
		}

		// 상태를 기반으로 위치정보를 저장합니다.
		// "taxidriver:location:normal:available"   5) "taxidriver:location:venti:ondriving"
		String locationKey = RedisKeyParser.getTaxiDriverLocationKeyBy(status, taxiType);
		redisService.setLocation(locationKey, taxiDriverLoginId,
			location);
		log.info("[MessagingService sendMessage : 위치정보 저장] 택시기사아이디 : {}, 레디스 키 : {} ", taxiDriverLoginId, locationKey);

		// Redis 에 택시별 위치정보 시간 기록 => 추후 택시기사 정상 여부 검증에 사용합니다.
		String currentTime = String.valueOf(System.currentTimeMillis());

		//  String.format("taxidriver:%s:lastupdate", taxiDriverLoginId);
		String updateTimeKey = RedisKeyParser.taxiDriverLastUpdate(taxiDriverLoginId);
		redisService.setValueWithTTL(updateTimeKey, currentTime, Duration.ofMinutes(5));
		log.info("[MessagingService sendMessage : 위치정보 갱신시간 저장] 택시기사아이디 : {}, 레디스 키 : {} ", taxiDriverLoginId,
			updateTimeKey);

		// 예약중이거나, 운행중인 경우 유저에게도 직접 전달합니다.
		if (status == TaxiDriverStatus.RESERVED || status == TaxiDriverStatus.ON_DRIVING) {
			log.info("[MessagingService sendMessage => 유저에게 위치 전송] 택시기사아이디 : {}, 택시기사상태 : {}, 택시타입 : {},  고객아이디 : {} ",
				taxiDriverLoginId, status, taxiType, customerLoginId);

			if (customerLoginId == null) {
				throw new CustomerNotAssignedException();
			}
			// /user/queue/update-taxidriver-location
			messagingTemplate.convertAndSendToUser(customerLoginId, LOCATION_TO_USER, response);
		}

	}

	public void sendDrivingStartMessageToUser(String customerLoginId, boolean isDrivingStarted,
		boolean isDrivingFinished) {
		messagingTemplate.convertAndSendToUser(
			customerLoginId,
			DISPATCH_DRIVING_STATUS_MESSAGE,
			new DrivingInfoResponse(isDrivingStarted, isDrivingFinished)
		);
	}

	public void sendDrivingStartMessageToTaxiDriver(String taxiDriverLoginId, boolean isDrivingStarted,
		boolean isDrivingFinished) {
		messagingTemplate.convertAndSendToUser(
			taxiDriverLoginId,
			DISPATCH_DRIVING_STATUS_MESSAGE,
			new DrivingInfoResponse(isDrivingStarted, isDrivingFinished)
		);
	}

	public void sendDrivingCompletedMessageToUser(String customerLoginId, DrivingSummaryResponse response) {
		messagingTemplate.convertAndSendToUser(
			customerLoginId,
			DISPATCH_DRIVING_STATUS_MESSAGE,
			response
		);
	}

	public void sendDrivingCompletedMessageToTaxiDriver(String taxiDriverLoginId, DrivingSummaryResponse response) {
		log.info("[sendDrivingCompletedMessageToTaxiDriver.MessagingService] 택시 기사에게 손님 하차 응답 메세지 전송.  콜 ID : {}, 택시기사 ID : {}", response.getCallId(),
			taxiDriverLoginId);

		log.info("DrivingSummaryResponse : {}", response.toString());
		messagingTemplate.convertAndSendToUser(
			taxiDriverLoginId,
			DISPATCH_DRIVING_RESULT_MESSAGE,
			response
		);
	}

	public void sendDispatchFailMessageToUser(String customerLoginId) {
		messagingTemplate.convertAndSendToUser(
			customerLoginId,
			DISPATCH_FAIL_MESSAGE_TO_USER,
			new ErrorResponse("배차실패", "택시와 연결이 끊어졌습니다. 다시 배차를 시도합니다.")
		);
	}

	public void sendDispatchFailMessageToTaxiDriver(String driverLoginId) {
		messagingTemplate.convertAndSendToUser(
			driverLoginId,
			DISPATCH_FAIL_MESSAGE_TO_TAXI_DRIVER,
			new ErrorResponse("배차취소", "위치 미전송으로 인해 배차가 취소되었습니다.")
		);
	}

	public void notifyDispatchSuccessToCustomer(String customerLoginId, String driverLoginId) {
		TaxiDriverStatus driverStatus = redisService.getDriverStatus(driverLoginId);
		messagingTemplate.convertAndSendToUser(
			customerLoginId,
			RIDE_STATUS_TO_USER,
			new MatchingSuccessResponse(driverStatus, driverLoginId)
		);
	}

	public void notifyDispatchFailedToCustomer(String customerLoginId) {
		log.info(
			"[notifyDispatchFailedToCustomer.MessagingService 호출] 범위 내에 택시가 없습니다. 배차 취소 메세지 전송. 고객 아이디 : {}, 목적지 : {}",
			customerLoginId, DISPATCH_FAIL_MESSAGE_TO_USER);
		messagingTemplate.convertAndSendToUser(
			customerLoginId,
			DISPATCH_FAIL_MESSAGE_TO_USER,
			new ErrorResponse("배차취소", "범위 내에 택시가 없습니다. 잠시 후에 시도해주세요.")
		);
	}

	public void sendCallMessageToTaxiDriver(String driverLoginId, CallMessage callMessage) {
		String destination = "/queue/call";
		messagingTemplate.convertAndSendToUser(
			driverLoginId,                        // 사용자 이름(Principal name)
			destination,                        // 목적지
			callMessage);                        // 전송할 메세지
	}
}
