package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.entity.CallInfo;
import goorm.humandelivery.domain.model.entity.Customer;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.internal.CallMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CallMessageRequest {

	@NotBlank(message = "출발 위치를 입력해 주세요.")
	private  Location expectedOrigin;
	@NotBlank(message = "도착 위치를 입력해 주세요.")
	private  Location expectedDestination;
	@NotBlank(message = "택시 타입을 선택해 주세요.")
	private  TaxiType taxiType;
	private  Integer retryCount;

	public CallMessage toQueueMessage(Long callId, String customerLoginId) {
		return new CallMessage(callId, customerLoginId, expectedOrigin, expectedDestination, taxiType, retryCount);
	}

	public CallInfo toCallInfo(Customer customer) {
		return new CallInfo(null, customer, expectedOrigin, expectedDestination, taxiType);
	}
}
