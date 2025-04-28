package goorm.humandelivery.application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import goorm.humandelivery.domain.model.entity.Taxi;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.domain.model.internal.QueueMessage;
import goorm.humandelivery.domain.model.response.CallTargetTaxiDriverDto;
import goorm.humandelivery.domain.model.response.TaxiDriverResponse;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;

// 책임: 메세지 큐와 관련된 로직만을 처리하는 전담 서비스
@Service
public class CallMessageQueueService implements MessageQueueService {

	private final BlockingQueue<QueueMessage> messageQueue = new LinkedBlockingQueue<>();
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private TaxiDriverRepository taxiDriverRepository;

	@Override
	public void enqueue(QueueMessage message) {
		messageQueue.offer(message);
	}

	// public QueueMessage take() throws InterrupedException{
	// 	return messageQueue.take();
	// }

	@Override
	public void processMessage(){
		while(!messageQueue.isEmpty()){
			QueueMessage message = messageQueue.poll();

			processMessage(message);
		}
	}

	public List<CallTargetTaxiDriverDto> findAvailableTaxiDrivers(String expectedOrigin) {
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

	public void sendCallMessageToTaxiDriver(CallTargetTaxiDriverDto taxiDriver, CallMessage callMessage) {

	}

}
