package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import goorm.humandelivery.domain.model.entity.RequestStatus;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusRequest;
import goorm.humandelivery.domain.model.request.UpdateTaxiDriverStatusResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@MessageMapping("/taxi-driver")  // "/app/taxi-driver"

public class WebSocketTaxiDriverController {

	@MessageMapping("/hello")  // /app/taxi-driver/hello
	@SendTo("/topic/hello") // 서버 -> 클라이언트로 전달하는 메세지 통로
	public String hello(String message) {
		log.info("서버 hello() 진입: {} ", message);
		return message.toUpperCase();
	}

	@MessageMapping("/location")
	@SendTo("/topic/hello") // 구독자 모두에 ㄷ뿌리는거.
	public String location(String message, Principal principal) {
		// 이걸 레디스에 저장한다 ~
		return message;
	}

	@MessageMapping("/update-status")
	@SendToUser("/queue/taxi-driver-status")
	public UpdateTaxiDriverStatusResponse updateStatus(Principal principal, UpdateTaxiDriverStatusRequest request) {

		log.info("[updateStatus 호출] taxiDriverId : {}, 상태 : {} 으로 변경요청", principal.getName(), request.getStatus());
		UpdateTaxiDriverStatusResponse response = new UpdateTaxiDriverStatusResponse();

		/**
		 * DB에서 상태변경
		 *
		 * 예외가 발생..
		 *
		 */
		response.setRequestStatus(RequestStatus.OK);
		response.setTaxiDriverStatus(TaxiDriverStatus.valueOf(request.getStatus()));


		return response;
	}

}
