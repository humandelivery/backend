package goorm.humandelivery.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import goorm.humandelivery.domain.model.internal.QueueMessage;

// 책임: 메세지 큐와 관련된 로직만을 처리하는 전담 서비스
@Service
public class MessageQueueService {

	private final BlockingQueue<QueueMessage> messageQueue = new LinkedBlockingQueue<>();
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public void enqueue(QueueMessage queueMessage) {
		messageQueue.offer(queueMessage);
	}

	// public QueueMessage take() throws InterrupedException{
	// 	return messageQueue.take();
	// }

	public void processMessage(){
		while(!messageQueue.isEmpty()){
			QueueMessage message = messageQueue.poll();

			processMessage(message);
		}
	}

	public void processMessage(QueueMessage message){
		// 메세지 처리
	}

}
