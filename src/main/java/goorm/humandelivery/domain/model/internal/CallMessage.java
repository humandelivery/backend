package goorm.humandelivery.domain.model.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CallMessage implements QueueMessage {

	private final String senderId;
	private final String expectedOrigin;
	private final String expectedDestination;
	private final String taxiType;

}
