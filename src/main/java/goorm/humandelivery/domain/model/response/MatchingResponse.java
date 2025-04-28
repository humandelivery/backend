package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.Location;

public class MatchingResponse {

	private String customerName;
	private String customerLoginId;
	private String customerPhoneNumber;
	private Location expectedOrigin;
	private Location expectedDestination;
}
