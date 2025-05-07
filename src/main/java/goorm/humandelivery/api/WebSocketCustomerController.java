package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import goorm.humandelivery.application.WebSocketCustomerService;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.request.CallCancelRequest;
import goorm.humandelivery.domain.model.request.CallMessageRequest;
import goorm.humandelivery.domain.model.request.MatchCancelRequest;
import goorm.humandelivery.domain.model.response.CallCancelResponse;
import goorm.humandelivery.domain.model.response.CallRequestMessageResponse;
import goorm.humandelivery.domain.model.response.CallTargetTaxiDriverDto;
import goorm.humandelivery.domain.model.response.MatchCancelResponse;
import goorm.humandelivery.domain.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 책임: 웹소켓 연결 및 클라이언트와의 상호작용을 담당하는 컨트롤러
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketCustomerController {

	private final WebSocketCustomerService webSocketCustomerService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/call/request")
	@SendToUser("/queue/call/response")
	public CallRequestMessageResponse handleMessage(CallMessageRequest request, Principal principal) {
		log.info("서버에서 승객의 콜 수신");
		Long callId = webSocketCustomerService.processMessage(request, principal.getName());

		return new CallRequestMessageResponse(callId, "콜이 성공적으로 요청되었습니다. Call ID : " + callId);
	}

	@MessageMapping("/call/cancel/request")
	@SendToUser("/queue/match/cancel/response")
	public CallCancelResponse cancelCall(CallCancelRequest request){
		log.info("콜 취소 요청 수신 완료");
		Long callId = request.getCallId();
		webSocketCustomerService.deleteCallById(callId);

		return new CallCancelResponse(callId, "콜이 취소 되었습니다. Call ID : " + callId);
	}

	@MessageMapping("/match/cancel/request")
	@SendToUser("/queue/match/cancel/response")
	public MatchCancelResponse cancelMatching(MatchCancelRequest request){
		log.info("배차 취소 요청 수신 완료");
		Long callId = request.getCallId();


		return new MatchCancelResponse(callId, "배차가 취소 되었습니다. Call ID : " + callId);
	}
}
