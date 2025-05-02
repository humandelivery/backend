package goorm.humandelivery.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.domain.repository.DrivingInfoRepository;

@Service
@Transactional(readOnly = true)
public class DrivingInfoService {

	private final DrivingInfoRepository drivingInfoRepository;

	@Autowired
	public DrivingInfoService(DrivingInfoRepository drivingInfoRepository) {
		this.drivingInfoRepository = drivingInfoRepository;
	}



}
