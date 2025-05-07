package goorm.humandelivery.infrastructure.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import goorm.humandelivery.api.WebSocketCustomerController;
import goorm.humandelivery.application.WebSocketCustomerService;
import goorm.humandelivery.common.exception.NoAvailableTaxiException;
import goorm.humandelivery.domain.model.entity.CallStatus;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.internal.QueueMessage;
import goorm.humandelivery.domain.model.response.CallTargetTaxiDriverDto;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;
import goorm.humandelivery.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 책임: 메세지 큐와 관련된 로직만을 처리하는 전담 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class BlockingMessageQueueService implements MessageQueueService {

	private final BlockingQueue<QueueMessage> blockingMessageQueue = new LinkedBlockingQueue<>();
	private final WebSocketCustomerService webSocketCustomerService;
	private final RedisService redisService;
	private final SimpMessagingTemplate messagingTemplate;


	@Override
	public void enqueue(QueueMessage message) {
		blockingMessageQueue.offer(message);
	}

	@Override
	@Scheduled(fixedDelay = 1000)
	public void processMessage(){
		while(!blockingMessageQueue.isEmpty()){
			QueueMessage message = blockingMessageQueue.poll();
			processMessage(message);
		}
	}

	@Override
	public void processMessage(QueueMessage message){
		// 메세지 처리
		if(!(message instanceof CallMessage)){
			// 메세지가 콜 메세지가 아닌 경우 처리
			return;
		}

		CallMessage callMessage = (CallMessage)message;

		// 1. 출발 위치에서 10분 거리 내의 운행 가능한 택시 목록 찾기
		int radiusInKm = 5*callMessage.getRetryCount();

		List<String> availableTaxiDrivers
			= redisService.findNearByAvailableDrivers(
			callMessage.getTaxiType(),
			callMessage.getExpectedOrigin().getLatitude(),
			callMessage.getExpectedOrigin().getLongitude(),
			radiusInKm);

		log.info("범위 내 유효한 택시의 수 : {}", availableTaxiDrivers.size());

		if(availableTaxiDrivers.isEmpty()){
			// 여겨시 택시 수가 0인경우 없다는 메세지를 고객에게 전달.
			log.info("범위 내에 유효한 택시가 없음");
			webSocketCustomerService.deleteCallById(callMessage.getCallId());
			throw new NoAvailableTaxiException();
		}

		// 2. 해당 택시기사들에게 메세지 전송
		redisService.setCallWith(callMessage.getCallId(), CallStatus.SENT);
		for (String taxiDriverLonginId : availableTaxiDrivers) {
			sendCallMessageToTaxiDriver(taxiDriverLonginId, callMessage);
		}
		log.info("유효한 택시기사에게 콜 요청 전송 완료");
	}

	public void sendCallMessageToTaxiDriver(String driverLoginId, CallMessage callMessage) {
		String destination = "/queue/call";
		messagingTemplate.convertAndSendToUser(
			driverLoginId,						// 사용자 이름(Principal name)
			destination, 						// 목적지
			callMessage);						// 전송할 메세지
	}
}
