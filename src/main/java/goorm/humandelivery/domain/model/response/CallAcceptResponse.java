package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.RequestStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CallAcceptResponse {


	RequestStatus requestStatus;
	MatchingResponse matchingResponse;


}
