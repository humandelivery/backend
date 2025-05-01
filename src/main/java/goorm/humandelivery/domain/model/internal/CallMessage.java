package goorm.humandelivery.domain.model.internal;

import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CallMessage extends QueueMessage {

	private Long callId;
	//private String senderId;
	private Location expectedOrigin;
	private Location expectedDestination;
	private TaxiType taxiType;
	private Integer retryCount;

}
