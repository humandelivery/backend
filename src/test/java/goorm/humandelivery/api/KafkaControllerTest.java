package goorm.humandelivery.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.internal.CallMessage;
import goorm.humandelivery.infrastructure.messaging.KafkaMessageQueueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KafkaControllerTest {
	private final KafkaMessageQueueService messageQueueService;

	@PostMapping("/test-call")
	public String testKafkaCall(){
		CallMessage msg = new CallMessage(
			1L,
			new Location(1.0,1.1),
			new Location(2.2,2.3),
			TaxiType.VENTI);
		messageQueueService.enqueue(msg);
		return "전송 완료";
	}

}
