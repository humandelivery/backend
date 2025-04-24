package goorm.humandelivery.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.domain.repository.TaxiDriverRepository;

@Service
@Transactional(readOnly = true)
public class TaxiDriverService {

	private final TaxiDriverRepository taxiDriverRepository;

	@Autowired
	public TaxiDriverService(TaxiDriverRepository taxiDriverRepository) {
		this.taxiDriverRepository = taxiDriverRepository;
	}
}
