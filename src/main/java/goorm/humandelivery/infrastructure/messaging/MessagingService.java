package goorm.humandelivery.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import goorm.humandelivery.common.exception.CustomerNotAssignedException;
import goorm.humandelivery.common.exception.OffDutyLocationUpdateException;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import goorm.humandelivery.domain.model.request.LocationResponse;
import goorm.humandelivery.infrastructure.redis.RedisKeyParser;
import goorm.humandelivery.infrastructure.redis.RedisService;

@Service
public class MessagingService {

	private static final String TAXI_DRIVER_LOCATION_UPDATE_DEST = "/queue/update-taxidriver-location";
	private final SimpMessagingTemplate messagingTemplate;
	private final RedisService redisService;

	@Autowired
	public MessagingService(SimpMessagingTemplate messagingTemplate, RedisService redisService) {
		this.messagingTemplate = messagingTemplate;
		this.redisService = redisService;
	}

	public void sendLocationToCustomer(String taxiDriverLoginId, String customerLoginId, Location location,
		TaxiDriverStatus status) {

		LocationResponse response = new LocationResponse(location);

		switch (status) {
			case OFF_DUTY -> throw new OffDutyLocationUpdateException();
			case AVAILABLE ->
				redisService.setLocation(RedisKeyParser.TAXI_DRIVER_LOCATION_KEY, location, taxiDriverLoginId);
			case RESERVED, ON_DRIVING -> {
				if (customerLoginId == null) {
					throw new CustomerNotAssignedException();
				}
				messagingTemplate.convertAndSendToUser(
					customerLoginId,
					TAXI_DRIVER_LOCATION_UPDATE_DEST,
					response);
			}
		}
	}
}
