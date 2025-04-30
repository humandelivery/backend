package goorm.humandelivery.api;

import java.security.Principal;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.request.CallAcceptRequest;
import goorm.humandelivery.domain.model.request.CallRejectRequest;
import goorm.humandelivery.domain.model.request.CallRejectResponse;
import goorm.humandelivery.domain.model.request.UpdateLocationRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusResponse;
import goorm.humandelivery.domain.model.response.CallAcceptResponse;
import goorm.humandelivery.infrastructure.messaging.MessagingService;
import goorm.humandelivery.infrastructure.redis.RedisKeyParser;
import goorm.humandelivery.infrastructure.redis.RedisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@MessageMapping("/taxi-driver")  // "/app/taxi-driver"
public class WebSocketTaxiDriverController {

	private final RedisService redisService;
	private final TaxiDriverService taxiDriverService;
	private final MessagingService messagingService;

	@Autowired
	public WebSocketTaxiDriverController(RedisService redisService, TaxiDriverService taxiDriverService,
		MessagingService messagingService) {
		this.redisService = redisService;
		this.taxiDriverService = taxiDriverService;
		this.messagingService = messagingService;
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

		String taxiDriverLoginId = principal.getName();
		String statusTobe = request.getStatus();
		log.info("[updateStatus 호출] taxiDriverId : {}, 상태 : {} 으로 변경요청", taxiDriverLoginId, statusTobe);

		// 1. DB에 상태 업데이트
		TaxiDriverStatus changedStatus = taxiDriverService.changeStatus(taxiDriverLoginId, statusTobe);

		// 2. 택시타입 조회
		TaxiType taxiType = taxiDriverService.findTaxiDriverTaxiType(taxiDriverLoginId).getTaxiType();

		// 3. Redis 에 택시기사 상태 상태 저장. TTL : 1시간
		String statusKey
			= RedisKeyParser.taxiDriverStatus(principal.getName());
		redisService.setValueWithTTL(statusKey, changedStatus.name(), Duration.ofHours(1));

		// 4. Redis 에 택시기사의 택시 종류 저장. TTL : 1일
		String taxiTypeKey = RedisKeyParser.taxiDriversTaxiType(taxiDriverLoginId);
		redisService.setValueWithTTL(taxiTypeKey, taxiType.name(), Duration.ofDays(1));

		return new UpdateTaxiDriverStatusResponse(changedStatus);
	}

	/**
	 * 택시운전기사 위치정보 업데이트 : 운행중 상태인 경우
	 * @param request
	 * @param principal
	 * @return UpdateLocationResponse
	 */
	@MessageMapping("/update-location")
	public void updateLocation(@Valid UpdateLocationRequest request, Principal principal) {
		String taxiDriverLoginId = principal.getName();
		String customerLoginId = request.getCustomerLoginId();
		Location location = request.getLocation();
		log.info("[updateLocation 호출] taxiDriverId : {}, 위도 : {}, 경도 : {}",
			principal.getName(),
			location.getLatitude(),
			location.getLongitude());

		TaxiDriverStatus status = taxiDriverService.getCurrentTaxiDriverStatus(taxiDriverLoginId);
		TaxiType taxiType = taxiDriverService.getCurrentTaxiType(taxiDriverLoginId);

		// 고객아이디, 택시기사 로케이션
		messagingService.sendLocationToCustomer(taxiDriverLoginId, status, taxiType, customerLoginId, location);
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
		 * TODO : 택시 요청 수락 -> Redis 저장 -> 성공 실패 여부 응답으로 줘야함. -> 성공 시? 배차 -> 운행정보까지 완료시켜야함.
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
}
