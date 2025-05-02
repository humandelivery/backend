package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import goorm.humandelivery.application.CallInfoService;
import goorm.humandelivery.application.DrivingInfoService;
import goorm.humandelivery.application.MatchingService;
import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.common.exception.CallAlreadyCompletedException;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
import goorm.humandelivery.domain.model.entity.CallStatus;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.request.CallAcceptRequest;
import goorm.humandelivery.domain.model.request.CallRejectRequest;
import goorm.humandelivery.domain.model.request.CallRejectResponse;
import goorm.humandelivery.domain.model.request.CreateMatchingRequest;
import goorm.humandelivery.domain.model.request.UpdateLocationRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusResponse;
import goorm.humandelivery.domain.model.response.CallAcceptResponse;
import goorm.humandelivery.infrastructure.messaging.MessagingService;
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
	private final MatchingService matchingService;
	private final CallInfoService callInfoService;
	private final DrivingInfoService drivingInfoService;

	@Autowired
	public WebSocketTaxiDriverController(RedisService redisService, TaxiDriverService taxiDriverService,
		MessagingService messagingService, MatchingService matchingService, CallInfoService callInfoService,
		DrivingInfoService drivingInfoService) {
		this.redisService = redisService;
		this.taxiDriverService = taxiDriverService;
		this.messagingService = messagingService;
		this.matchingService = matchingService;
		this.callInfoService = callInfoService;
		this.drivingInfoService = drivingInfoService;
	}

	/**
	 * 택시운전기사 상태 변경
	 * @param request
	 * @param principal
	 * @return UpdateTaxiDriverStatusResponse
	 */
	@MessageMapping("/update-status")
	@SendToUser("/queue/taxi-driver-status")
	public UpdateTaxiDriverStatusResponse updateStatus(@Valid @RequestBody UpdateTaxiDriverStatusRequest request,
		Principal principal) {

		String taxiDriverLoginId = principal.getName();
		String statusTobe = request.getStatus();
		log.info("[updateStatus 호출] taxiDriverId : {}, 상태 : {} 으로 변경요청", taxiDriverLoginId, statusTobe);

		// 1. DB에 상태 업데이트
		TaxiDriverStatus changedStatus = taxiDriverService.changeStatus(taxiDriverLoginId,
			TaxiDriverStatus.valueOf(statusTobe));

		// 2. 택시타입 조회
		TaxiType taxiType = taxiDriverService.findTaxiDriverTaxiType(taxiDriverLoginId).getTaxiType();

		// 3. redis 에 상태 업데이트
		return redisService.handleTaxiDriverStatusInRedis(taxiDriverLoginId, changedStatus, taxiType);
	}

	/**
	 * 택시운전기사 위치정보 업데이트
	 * @param request
	 * @param principal
	 * @return UpdateLocationResponse
	 */
	@MessageMapping("/update-location")
	public void updateLocation(@Valid @RequestBody UpdateLocationRequest request, Principal principal) {
		String taxiDriverLoginId = principal.getName();
		String customerLoginId = request.getCustomerLoginId();
		Location location = request.getLocation();
		log.info("[updateLocation 호출] taxiDriverId : {}, 위도 : {}, 경도 : {}",
			principal.getName(),
			location.getLatitude(),
			location.getLongitude());

		// redis 에서 택시기사 상태조회 -> 없으면 DB 조회 -> redis 저장 -> 반환
		TaxiDriverStatus status = taxiDriverService.getCurrentTaxiDriverStatus(taxiDriverLoginId);

		// redis 에서 택시종류조회 -> 없으면 DB 조회 -> redis 저장 -> 반환
		TaxiType taxiType = taxiDriverService.getCurrentTaxiType(taxiDriverLoginId);

		if (status == TaxiDriverStatus.OFF_DUTY) {
			throw new OffDutyLocationUpdateException();
		}

		// 택시기사 위치정보 저장
		messagingService.sendMessage(taxiDriverLoginId, status, taxiType, customerLoginId, location);
	}

	/**
	 * 콜 요청 수락
	 * @param request
	 * @param principal
	 */
	@MessageMapping("/accept-call")
	@SendToUser("/queue/accept-call-result")
	public CallAcceptResponse acceptTaxiCall(@Valid @RequestBody CallAcceptRequest request, Principal principal) {

		Long callId = request.getCallId();
		String taxiDriverLoginId = principal.getName();

		CallStatus callStatus = redisService.getCallStatus(callId);

		// 이미 배차가 완료된 경우..
		if (callStatus != CallStatus.SENT) {
			log.info("[acceptTaxiCall.CallAcceptResponse] 완료된 콜에 대한 배차 신청. 택시기사 : {}, 콜ID : {}",
				taxiDriverLoginId, callId);
			throw new CallAlreadyCompletedException();
		}

		// 배차 등록 시도
		boolean isSuccess = redisService.tryAcceptCall(String.valueOf(callId), taxiDriverLoginId);
		if (!isSuccess) {
			log.info("[acceptTaxiCall.CallAcceptResponse] 완료된 콜에 대한 배차 신청. 택시기사 : {}, 콜ID : {}",
				taxiDriverLoginId, callId);
			throw new CallAlreadyCompletedException();
		}

		// 엔티티 생성
		Long taxiDriverId = taxiDriverService.findIdByLoginId(taxiDriverLoginId);
		matchingService.create(new CreateMatchingRequest(callId, taxiDriverId));

		TaxiType taxiType = redisService.getDriversTaxiType(taxiDriverLoginId);
		TaxiDriverStatus taxiDriverStatus = taxiDriverService.changeStatus(taxiDriverLoginId,
			TaxiDriverStatus.RESERVED);

		// 상태 변경에 따른 redis 처리
		redisService.assignCallToDriver(callId, taxiDriverLoginId);
		redisService.handleTaxiDriverStatusInRedis(taxiDriverLoginId, taxiDriverStatus, taxiType);

		// CallAcceptResponse 응답하기.
		CallAcceptResponse callAcceptResponse = callInfoService.getCallAcceptResponse(callId);
		log.info("[acceptTaxiCall.WebSocketTaxiDriverController] 배차완료.  콜 ID : {}, 고객 ID : {}, 택시기사 ID : {}",
			callId, callAcceptResponse.getCustomerLoginId(), taxiDriverId);

		// 고객에게 배차되엇다고 상태 전달하기
		messagingService.notifyDispatchSuccessToCustomer(callAcceptResponse.getCustomerLoginId(), taxiDriverLoginId);

		return callAcceptResponse;
	}

	/**
	 * 콜 요청 거절
	 * @param request
	 * @param principal
	 * @return
	 */
	@MessageMapping("/reject-call")
	@SendToUser("/queue/reject-call-result")
	public CallRejectResponse rejectTaxiCall(@Valid @RequestBody CallRejectRequest request, Principal principal) {
		log.info("[rejectTaxiCall.WebSocketTaxiDriverController] 콜 거절.  콜 ID : {}, 택시기사 ID : {}",
			request.getCallId(), principal.getName());

		// 해당 콜을 거절한 택시기사 집합에 추가
		redisService.addRejectedDriverToCall(request.getCallId(), principal.getName());
		return new CallRejectResponse(request.getCallId());
	}

	/**
	 * 승객 승차 완료 요청 처리
	 */
	@MessageMapping("/ride-start")
	@SendToUser("/queue/ride-status")
	public void startRideWithCustomer(@Valid @RequestBody CreateDrivingInfoRequest request, Principal principal) {

		/**
		 * 손님 타고, 운행정보 엔티티 생성
		 */

		// 매칭 조회해야함.
		Long matchingId = matchingService.findMatchingIdByCallId(request.getCallId());

		// 운행정보 생성
		Location taxiDriverLocation = redisService.getDriverLocation(principal.getName());


		// 1. 손님 탓으니까, 운행 시작
		// 운행정보 엔티티 생성

		// 택시 상태 변경
		// 레디스 상태 변경

		// 손님에게 운행 시작 메세지 전달
		// 택시에게 응답 반

	}
}
