package goorm.humandelivery.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.common.exception.CallInfoEntityNotFoundException;
import goorm.humandelivery.domain.model.response.CallAcceptResponse;
import goorm.humandelivery.domain.repository.CallInfoRepository;

@Service
@Transactional(readOnly = true)
public class CallInfoService {

	private final CallInfoRepository callInfoRepository;

	@Autowired
	public CallInfoService(CallInfoRepository callInfoRepository) {
		this.callInfoRepository = callInfoRepository;
	}

	public CallAcceptResponse getCallAcceptResponse(Long callId) {
		return callInfoRepository.findCallInfoAndCustomerByCallId(callId)
			.orElseThrow(CallInfoEntityNotFoundException::new);
	}

	public String findCustomerLoginIdById(Long id) {
		return callInfoRepository.findCustomerLoginIdByCallId(id).orElseThrow(CallInfoEntityNotFoundException::new);
	}
}
