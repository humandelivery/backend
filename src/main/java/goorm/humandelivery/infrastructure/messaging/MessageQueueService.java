package goorm.humandelivery.infrastructure.messaging;

import goorm.humandelivery.domain.model.internal.QueueMessage;

public interface MessageQueueService {


	public void enqueue(QueueMessage message);
	public void processMessage();
	public void processMessage(QueueMessage message);

}
