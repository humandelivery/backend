package goorm.humandelivery.domain.model.response;

import org.springframework.beans.factory.parsing.Location;

public class CallMessageResponse {
	private Long callId;
	private Location expectedOrigin;
	private Location expectedDestination;
}
