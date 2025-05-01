package goorm.humandelivery.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.common.exception.CallInfoEntityNotFoundException;
import goorm.humandelivery.common.exception.TaxiDriverEntityNotFoundException;
import goorm.humandelivery.domain.model.entity.CallInfo;
import goorm.humandelivery.domain.model.entity.Matching;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.model.request.CreateMatchingRequest;
import goorm.humandelivery.domain.repository.CallInfoRepository;
import goorm.humandelivery.domain.repository.MatchingRepository;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;

@Service
@Transactional(readOnly = true)
public class MatchingService {

	private final MatchingRepository matchingRepository;
	private final CallInfoRepository callRepository;
	private final TaxiDriverRepository taxiDriverRepository;

	@Autowired
	public MatchingService(MatchingRepository matchingRepository, CallInfoRepository callRepository,
		TaxiDriverRepository taxiDriverRepository) {
		this.matchingRepository = matchingRepository;
		this.callRepository = callRepository;
		this.taxiDriverRepository = taxiDriverRepository;
	}

	@Transactional
	public void create(CreateMatchingRequest request) {

		Long callId = request.getCallId();
		Long taxiDriverId = request.getTaxiDriverId();

		CallInfo callInfo = callRepository.findById(callId).orElseThrow(CallInfoEntityNotFoundException::new);
		TaxiDriver taxiDriver = taxiDriverRepository.findById(taxiDriverId)
			.orElseThrow(TaxiDriverEntityNotFoundException::new);

		Matching matching = Matching.builder()
			.callInfo(callInfo)
			.taxiDriver(taxiDriver).build();

		matchingRepository.save(matching);
	}
}
