package goorm.humandelivery.infrastructure.messaging;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.internal.QueueMessage;
import goorm.humandelivery.domain.model.response.CallTargetTaxiDriverDto;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaMessageQueueService implements MessageQueueService{

	private final TaxiDriverRepository taxiDriverRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final KafkaMessageProducer kafkaMessageProducer;

	@Override
	public void enqueue(QueueMessage message){
		kafkaMessageProducer.send(message);
	};

	@Override
	public void processMessage(){ // 카프카 쓸때는 안쓰는 메서드
	};

	@Override
	public void processMessage(QueueMessage message){
		// 메세지 처리
		if(!(message instanceof CallMessage)){
			// 메세지가 콜 메세지가 아닌 경우 처리
			return;
		}

		CallMessage callMessage = (CallMessage) message;

		// 1. 출발 위치에서 10분 거리 내의 운행 가능한 택시 목록 찾기
		List<CallTargetTaxiDriverDto> availableTaxiDrivers = findAvailableTaxiDrivers(callMessage.getExpectedOrigin());

		// 2. 해당 택시기사들에게 메세지 전송
		for(CallTargetTaxiDriverDto taxiDriver : availableTaxiDrivers) {
			sendCallMessageToTaxiDriver(taxiDriver, callMessage);
		}

	}

	public List<CallTargetTaxiDriverDto> findAvailableTaxiDrivers(Location expectedOrigin) {
		// 택시 목록 작성
		// 실제 코드는 출발 예상지를 기준으로 레디스에서 "빈차"상태이면서 10분 거리 내에 있는 택시를 찾을거임.

		// 현재는 테스트를 위해 목 택시 목록을 만들 거임.
		List<TaxiDriver> taxiDrivers = taxiDriverRepository.findAll();
		List<CallTargetTaxiDriverDto> callTargetTaxiDriverDtos = new ArrayList<>();

		for(TaxiDriver taxiDriver : taxiDrivers) {
			callTargetTaxiDriverDtos.add(CallTargetTaxiDriverDto.from(taxiDriver));
		}

		return callTargetTaxiDriverDtos;
	}

	public void sendCallMessageToTaxiDriver(CallTargetTaxiDriverDto taxiDriver, CallMessage callMessage) {
		String destination = "/queue/call";
		String driverLoginId = taxiDriver.getDriverLoginId();
		messagingTemplate.convertAndSendToUser(
			driverLoginId,						// 사용자 이름(Principal name)
			destination, 						// 목적지
			callMessage);						// 전송할 메세지

	}
}
