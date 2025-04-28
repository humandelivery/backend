package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.entity.RequestStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallRejectResponse {

	private RequestStatus requestStatus;
	private Long callId;
}
