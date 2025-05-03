package goorm.humandelivery.application;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.common.exception.MatchingEntityNotFoundException;
import goorm.humandelivery.domain.model.entity.DrivingInfo;
import goorm.humandelivery.domain.model.entity.DrivingStatus;
import goorm.humandelivery.domain.model.entity.Location;
import goorm.humandelivery.domain.model.entity.Matching;
import goorm.humandelivery.domain.model.request.CreateDrivingInfoRequest;
import goorm.humandelivery.domain.repository.DrivingInfoRepository;
import goorm.humandelivery.domain.repository.MatchingRepository;

@Service
@Transactional(readOnly = true)
public class DrivingInfoService {

	private final DrivingInfoRepository drivingInfoRepository;
	private final MatchingRepository matchingRepository;

	@Autowired
	public DrivingInfoService(DrivingInfoRepository drivingInfoRepository, MatchingRepository matchingRepository) {
		this.drivingInfoRepository = drivingInfoRepository;
		this.matchingRepository = matchingRepository;
	}

	@Transactional
	public DrivingInfo create(CreateDrivingInfoRequest request) {

		Location departPosition = request.getDepartPosition();

		Long matchingId = request.getMatchingId();
		Matching matching = matchingRepository.findById(matchingId)
			.orElseThrow(MatchingEntityNotFoundException::new);


		LocalDateTime now = LocalDateTime.now();

		DrivingInfo drivingInfo = DrivingInfo.builder()
			.matching(matching)
			.origin(departPosition)
			.pickupTime(now)
			.drivingStatus(DrivingStatus.ON_DRIVING)
			.reported(false)
			.build();

		return drivingInfoRepository.save(drivingInfo);
	}
}
