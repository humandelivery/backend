package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
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

		// OFF_DUTY 면 Redis 에서 다 제거.
		if (changedStatus == TaxiDriverStatus.OFF_DUTY) {
			// 운행 종료. active 택시기사 목록에서 제외
			log.info("[updateStatus : 택시기사 비활성화. active 목록에서 제외] taxiDriverId : {}, 상태 : {}, ", taxiDriverLoginId,
				statusTobe);
			redisService.setOffDuty(taxiDriverLoginId);
			return new UpdateTaxiDriverStatusResponse(changedStatus);
		}

		// 3. Redis 에 택시기사 상태 저장. TTL : 1시간
		log.info("[updateStatus : redis 택시기사 상태 저장] taxiDriverId : {}, 상태 : {}, ", taxiDriverLoginId, statusTobe);
		redisService.setDriversStatus(taxiDriverLoginId, changedStatus);

		// 4. Redis 에 택시기사의 택시 종류 저장. TTL : 1일
		log.info("[updateStatus : redis 택시기사 종류 저장] taxiDriverId : {}, 상태 : {}, ", taxiDriverLoginId, statusTobe);
		redisService.setDriversTaxiType(taxiDriverLoginId, taxiType);

		log.info("[updateStatus : redis 택시기사 active set 저장] taxiDriverId : {}, 상태 : {}, ", taxiDriverLoginId,
			statusTobe);
		// active driver set 에 없으면 추가
		redisService.setActive(taxiDriverLoginId);

		return new UpdateTaxiDriverStatusResponse(changedStatus);
	}

	/**
	 * 택시운전기사 위치정보 업데이트
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
	@MessageMapping("/taxi-driver/accept-call")
	@SendToUser("/queue/accept-call-result")
	public CallAcceptResponse acceptTaxiCall(CallAcceptRequest request, Principal principal) {

		/**
		 * TODO : 택시 요청 수락 -> Redis 저장 -> 성공 실패 여부 응답으로 줘야함. -> 성공 시? 배차 -> 운행정보까지 완료시켜야함.
		 */

		Long callId = request.getCallId();
		String taxiDriverLoginId = principal.getName();

		boolean isSuccess = redisService.setValueIfAbsent(String.valueOf(callId), taxiDriverLoginId);

		/**
		 * 1.
		 * 콜 생성 -> 택시기사에게 전달 -> 택시기사 콜 응답 -> 레디스 저장 성공 -> 배차 생성 -> 운행 생성 -> 택시기사 상태 반경 -> 택시기사에게 배차완료 응답 전송
		 *
		 * 2.
		 * 콜 생성 -> 택시기사에게 전달 -> 택시기사 콜 응답  -> 레디스 저장 성공 -> 배차 생성 ->  택시기사 응답이 없으면?? 택시기사 연결이 끊기면??
		 *  예약중상태에서 택시기사 위치정보 갱신이 안되거나 하면 예약 취소해야함.
		 *
		 * 배차 생성되면.......배차 생성되면.....
		 */

		/**
		 * TODO : 배차 후에는 available에서 해당 택시기사 지워야함.
		 */
		if (!isSuccess) {
			// 택시기사에게 배차 실패 메세지 보내기
		}

		// 성공 시 배차 엔티티, 운행 엔티티 생성, 기사상태변경도 필요함.

		// 아래는 임시
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
