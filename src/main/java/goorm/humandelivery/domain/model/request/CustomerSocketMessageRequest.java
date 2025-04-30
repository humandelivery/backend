package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.internal.CallMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomerSocketMessageRequest {

	@NotBlank(message = "출발 위치를 입력해 주세요.")
	private final Location expectedOrigin;
	@NotBlank(message = "도착 위치를 입력해 주세요.")
	private final Location expectedDestination;
	@NotBlank(message = "택시 타입을 선택해 주세요.")
	private final TaxiType taxiType;

	public CallMessage toQueueMessage(String senderId) {
		return new CallMessage(senderId, expectedOrigin, expectedDestination, taxiType);
	}

}
