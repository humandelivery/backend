package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class CallAcceptResponse {

	private Long callId;
	private String customerName;
	private String customerLoginId;
	private String customerPhoneNumber;
	private Location expectedOrigin;
	private Location expectedDestination;

	public CallAcceptResponse(Long callId, String customerName, String customerLoginId, String customerPhoneNumber,
		Location expectedOrigin, Location expectedDestination) {
		this.callId = callId;
		this.customerName = customerName;
		this.customerLoginId = customerLoginId;
		this.customerPhoneNumber = customerPhoneNumber;
		this.expectedOrigin = expectedOrigin;
		this.expectedDestination = expectedDestination;
	}
}
