package goorm.humandelivery.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import goorm.humandelivery.domain.model.internal.QueueMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KafkaMessageQueueService implements MessageQueueService{

	private final KafkaTemplate<String, QueueMessage> kafkaTemplate;
	private final String topicName = "taxi-call-queue";

	@Override
	public void enqueue(QueueMessage message){
		kafkaTemplate.send(topicName, message);
	};

	@Override
	public void processMessage(){
	};

	@KafkaListener(topics = "taxi-call-queue", groupId = "call-group")
	public void listen(QueueMessage messgae){
		processMessage(messgae);
	}

	@Override
	public void processMessage(QueueMessage message){

	};
}
