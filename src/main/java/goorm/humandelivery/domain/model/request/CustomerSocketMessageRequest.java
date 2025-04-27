package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.internal.QueueMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomerSocketMessageRequest {

	@NotBlank(message = "출발 위치를 입력해 주세요.")
	private final String expectedOrigin;
	@NotBlank(message = "도착 위치를 입력해 주세요.")
	private final String expectedDestination;
	@NotBlank(message = "택시 타입을 선택해 주세요.")
	private final String taxiType;

	public QueueMessage toQueueMessage(String senderId) {
		return new QueueMessage(senderId, expectedOrigin, expectedDestination, taxiType);
	}

}
