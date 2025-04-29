package goorm.humandelivery.domain.model.internal;

import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CallMessage extends QueueMessage {

	private final String senderId;
	private final Location expectedOrigin;
	private final Location expectedDestination;
	private final TaxiType taxiType;

}
