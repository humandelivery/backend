package goorm.humandelivery.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchingRequest {
	private Long callId;
	private Long taxiDriverId;
}
