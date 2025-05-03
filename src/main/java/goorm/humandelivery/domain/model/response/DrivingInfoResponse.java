package goorm.humandelivery.domain.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DrivingInfoResponse {

	private boolean isDrivingStarted;

	public DrivingInfoResponse(boolean isDrivingStarted) {
		this.isDrivingStarted = isDrivingStarted;
	}
}
