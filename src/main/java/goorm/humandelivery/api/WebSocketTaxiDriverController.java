package goorm.humandelivery.api;

import java.security.Principal;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.request.CallAcceptRequest;
import goorm.humandelivery.domain.model.request.CallRejectRequest;
import goorm.humandelivery.domain.model.request.CallRejectResponse;
import goorm.humandelivery.domain.model.request.LocationResponse;
import goorm.humandelivery.domain.model.request.UpdateDrivingLocationRequest;
import goorm.humandelivery.domain.model.request.UpdateLocationRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusResponse;
import goorm.humandelivery.domain.model.response.CallAcceptResponse;
import goorm.humandelivery.infrastructure.redis.RedisKeyParser;
import goorm.humandelivery.infrastructure.redis.RedisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@MessageMapping("/taxi-driver")  // "/app/taxi-driver"

public class WebSocketTaxiDriverController {

	private final RedisService redisService;
	private final TaxiDriverService taxiDriverService;

	@Autowired
	public WebSocketTaxiDriverController(RedisService redisService, TaxiDriverService taxiDriverService) {
		this.redisService = redisService;
		this.taxiDriverService = taxiDriverService;
	}

	/**
	 * 택시운전기사 상태 변경
	 * @param request
	 * @param principal
	 * @return UpdateTaxiDriverStatusResponse
	 */
	@MessageMapping("/update-status")
	@SendToUser("/queue/taxi-driver-status")
	public UpdateTaxiDriverStatusResponse updateStatus(@Valid UpdateTaxiDriverStatusRequest request,
		Principal principal) {

		log.info("[updateStatus 호출] taxiDriverId : {}, 상태 : {} 으로 변경요청", principal.getName(), request.getStatus());

		// 1. DB에 상태 저장
		TaxiDriverStatus changedStatus = taxiDriverService.changeStatus(principal.getName(), request.getStatus());

		// 2. Redis 에 상태 저장. TTL : 1시간
		String key = RedisKeyParser.taxiDriverStatus(principal.getName());
		redisService.setValueWithTTL(key, changedStatus.name(), Duration.ofHours(1));

		return new UpdateTaxiDriverStatusResponse(changedStatus);
	}

	/**
	 * 택시운전기사 위치정보 업데이트 : 운행중 상태인 경우
	 * @param request
	 * @param principal
	 * @return UpdateLocationResponse
	 */
	@MessageMapping("/taxi-driver/update-location")
	@SendToUser("/queue/taxi-driver-location")
	public LocationResponse updateLocation(@Valid UpdateLocationRequest request, Principal principal) {
		Location location = request.getLocation();
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		log.info("[updateLocation 호출] taxiDriverId : {}", principal.getName());

		/**
		 * TODO : Redis에 택시아이디, 택시타입, 위치 저장 필요. 기사 아이디와 위치정보 저장.
		 */

		/**
		 * TODO : 저장 후 응답
		 */

		LocationResponse response = new LocationResponse();

		response.setLocation(location);
		return response;
	}

	/**
	 * 콜 요청 수락
	 * @param request
	 * @param principal
	 */
	@MessageMapping("/taxi-driver/accept-call")
	@SendToUser("/queue/accept-call-result")
	public CallAcceptResponse acceptTaxiCall(CallAcceptRequest request, Principal principal) {

		// 콜 수락 들어오면..?
		// 콜 아이디를 redis에 씀
		// redis에 쓴거 성공하면 -> 배차 완료임..
		// 배차는 내가 만듬.

		/**
		 * TODO : 택시 요청 수락 -> Redis 저장 -> 성공 실패 여부 응답으로 줘야함. -> 성공 시? 배차까지 완료시켜야함.
		 *
		 * 배차 성공 시 ?
		 * 배차 생성 -> 사용자에게 배차 완료 응답 줘야함.
		 *
		 * 수락이 되면? 정보로 배차 완료 확인  유저아이디, 출발예정위치, 도착예정위치 보내줘야함
		 */

		CallAcceptResponse response = new CallAcceptResponse();
		return response;
	}

	/**
	 * 콜 요청 거절
	 * @param request
	 * @param principal
	 * @return
	 */
	@MessageMapping("/taxi-driver/reject-call")
	@SendToUser("/queue/reject-call-result")
	public CallRejectResponse rejectTaxiCall(CallRejectRequest request, Principal principal) {

		/**
		 * TODO : 콜 요청 거절..
		 */

		CallRejectResponse response = new CallRejectResponse();
		response.setCallId(request.getCallId());
		return response;
	}

	@MessageMapping("/taxi-driver/update-driving-location")
	@SendToUser("/queue/driving-location")

	public LocationResponse updateDrivingLocation(UpdateDrivingLocationRequest request, Principal principal) {

		/**
		 * TODO : requeest에 현재 위치 담아서 주기적으로 보내주면, 손님에게 sendTo할거임. 손님 아이디는 dto로부터 가져옴?
		 * messagingTemplate.convertAndSendToUser(request.getCustomerLoginId, "/queue/passenger/location-update", locationData)
		 */
		Location location = request.getLocation();

		LocationResponse locationResponse = new LocationResponse();
		locationResponse.setLocation(location);

		return locationResponse;
	}

}
