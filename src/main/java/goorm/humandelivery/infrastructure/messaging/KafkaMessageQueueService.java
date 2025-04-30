package goorm.humandelivery.infrastructure.messaging;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.internal.QueueMessage;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;
import goorm.humandelivery.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaMessageQueueService implements MessageQueueService {

	private final TaxiDriverRepository taxiDriverRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final KafkaMessageProducer kafkaMessageProducer;
	private final RedisService redisService;

	@Override
	public void enqueue(QueueMessage message) {
		kafkaMessageProducer.send(message);
	}

	;

	@Override
	public void processMessage() { // 카프카 쓸때는 안쓰는 메서드
	}

	;

	@Override
	public void processMessage(QueueMessage message) {
		// 메세지 처리
		if (!(message instanceof CallMessage)) {
			// 메세지가 콜 메세지가 아닌 경우 처리
			return;
		}

		CallMessage callMessage = (CallMessage)message;

		// 1. 출발 위치에서 10분 거리 내의 운행 가능한 택시 목록 찾기
		List<String> availableTaxiDrivers
			= redisService.findNearByDrivers(
			callMessage.getTaxiType(),
			callMessage.getExpectedOrigin().getLatitude(),
			callMessage.getExpectedOrigin().getLongitude(),
			10.0);

		// 2. 해당 택시기사들에게 메세지 전송
		for (String taxiDriverLonginId : availableTaxiDrivers) {
			sendCallMessageToTaxiDriver(taxiDriverLonginId, callMessage);
		}

	}

	public void sendCallMessageToTaxiDriver(String taxiDriverLoginId, CallMessage callMessage) {
		String destination = "/queue/call";
		messagingTemplate.convertAndSendToUser(
			taxiDriverLoginId,                    // User: 사용자 이름(Principal name)
			destination,                        // Destination: 목적지
			callMessage);                        // Payload: 전송할 메세지

	}
}
