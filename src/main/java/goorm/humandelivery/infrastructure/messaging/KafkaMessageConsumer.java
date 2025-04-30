package goorm.humandelivery.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import goorm.humandelivery.domain.model.internal.QueueMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaMessageConsumer {
	private final KafkaMessageQueueService kafkaMessageQueueService;

	@KafkaListener(topics = "taxi-call-queue", groupId = "call-group")
	public void listen(QueueMessage message){
		kafkaMessageQueueService.processMessage(message);
	}
}
