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

	private String customerName;
	private String customerLoginId;
	private String customerPhoneNumber;
	private Location expectedOrigin;
	private Location expectedDestination;

	@Builder
	public CallAcceptResponse(String customerName, String customerLoginId, String customerPhoneNumber,
		Location expectedOrigin, Location expectedDestination) {
		this.customerName = customerName;
		this.customerLoginId = customerLoginId;
		this.customerPhoneNumber = customerPhoneNumber;
		this.expectedOrigin = expectedOrigin;
		this.expectedDestination = expectedDestination;
	}
}
