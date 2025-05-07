package goorm.humandelivery.infrastructure.messaging;

import java.util.List;

import org.springframework.stereotype.Service;

import goorm.humandelivery.application.CallInfoService;
import goorm.humandelivery.common.exception.NoAvailableTaxiException;
import goorm.humandelivery.domain.model.entity.CallStatus;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.internal.QueueMessage;
import goorm.humandelivery.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageQueueService implements MessageQueueService {

	private final KafkaMessageProducer kafkaMessageProducer;
	private final RedisService redisService;
	private final MessagingService messagingService;
	private final CallInfoService callInfoService;

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
		int radiusInKm = 5 * callMessage.getRetryCount();

		List<String> availableTaxiDrivers = redisService.findNearByAvailableDrivers(callMessage.getCallId(),
			callMessage.getTaxiType(), callMessage.getExpectedOrigin().getLatitude(),
			callMessage.getExpectedOrigin().getLongitude(), radiusInKm);

		log.info("범위 내 유효한 택시의 수 : {}", availableTaxiDrivers.size());

		if (availableTaxiDrivers.isEmpty()) {
			// 여겨시 택시 수가 0인경우 없다는 메세지를 고객에게 전달.
			log.info("범위 내에 유효한 택시가 없음");
			callInfoService.deleteCallById(callMessage.getCallId());
			messagingService.notifyDispatchFailedToCustomer(callMessage.getCustomerLoginId());
			throw new NoAvailableTaxiException();
		}

		// 2. 해당 택시기사들에게 메세지 전송
		redisService.setCallWith(callMessage.getCallId(), CallStatus.SENT);
		for (String taxiDriverLonginId : availableTaxiDrivers) {
			messagingService.sendCallMessageToTaxiDriver(taxiDriverLonginId, callMessage);
		}
		log.info("유효한 택시기사에게 콜 요청 전송 완료");
	}
}
