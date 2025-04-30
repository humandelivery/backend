package goorm.humandelivery.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import goorm.humandelivery.common.exception.CustomerNotAssignedException;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.request.LocationResponse;
import goorm.humandelivery.infrastructure.redis.RedisKeyParser;
import goorm.humandelivery.infrastructure.redis.RedisService;

@Service
public class MessagingService {

	private static final String LOCATION_TO_USER = "/queue/update-taxidriver-location";
	private final SimpMessagingTemplate messagingTemplate;
	private final RedisService redisService;

	@Autowired
	public MessagingService(SimpMessagingTemplate messagingTemplate, RedisService redisService) {
		this.messagingTemplate = messagingTemplate;
		this.redisService = redisService;
	}

	public void sendLocationToCustomer(String taxiDriverLoginId, TaxiDriverStatus status, TaxiType taxiType,
		String customerLoginId, Location location
	) {

		LocationResponse response = new LocationResponse(location);

		switch (status) {
			case OFF_DUTY -> throw new OffDutyLocationUpdateException();
			case AVAILABLE ->
				redisService.setLocation(
					RedisKeyParser.taxiDriverLocationKeyFrom(taxiType),
					taxiDriverLoginId,
					location);

			case RESERVED, ON_DRIVING -> {
				if (customerLoginId == null) {
					throw new CustomerNotAssignedException();
				}
				messagingTemplate.convertAndSendToUser(
					customerLoginId,
					LOCATION_TO_USER,
					response);
			}
		}
	}
}
