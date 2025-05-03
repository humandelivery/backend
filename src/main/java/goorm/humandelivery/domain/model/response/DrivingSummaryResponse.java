package goorm.humandelivery.domain.model.response;

import java.time.LocalDateTime;

import goorm.humandelivery.domain.model.entity.DrivingStatus;
import goorm.humandelivery.domain.model.entity.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DrivingSummaryResponse {

	private Long callId;
	private String customerLoginId;
	private String taxiDriverLoginId;
	private Location origin;
	private LocalDateTime pickupTime;
	private Location destination;
	private LocalDateTime arrivingTime;
	private DrivingStatus drivingStatus;
	private boolean reported;

}
