package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import goorm.humandelivery.application.WebSocketCustomerService;
import goorm.humandelivery.domain.model.request.CustomerSocketMessageRequest;
import lombok.RequiredArgsConstructor;


// 책임: 웹소켓 연결 및 클라이언트와의 상호작용을 담당하는 컨트롤러
@Controller
@RequiredArgsConstructor
public class WebSocketCustomerController {

	private final WebSocketCustomerService webSocketCustomerService;

	@MessageMapping("/") // Todo
	public void handleMessage(CustomerSocketMessageRequest request, Principal principal) {
		webSocketCustomerService.processMessage(request, principal.getName());
	}

}
