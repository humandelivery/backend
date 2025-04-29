package goorm.humandelivery.application;

import org.springframework.stereotype.Service;

import goorm.humandelivery.domain.model.request.CallMessageRequest;
import goorm.humandelivery.infrastructure.messaging.BlockingMessageQueueService;
import goorm.humandelivery.infrastructure.messaging.KafkaMessageQueueService;
import lombok.RequiredArgsConstructor;

// 책임: 웹소켓 연결 및 클라이언트와의 상호작용을 담당하는 서비스
@Service
@RequiredArgsConstructor
public class WebSocketCustomerService {

	// private final BlockingMessageQueueService messageQueueService;
	private final KafkaMessageQueueService messageQueueService;

	public void processMessage(CallMessageRequest request, String senderId) {
		messageQueueService.enqueue(request.toQueueMessage(senderId));
	}

}
