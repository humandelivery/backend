package goorm.humandelivery.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;

@Service
public class CustomTaxiDriverDetailService implements UserDetailsService {

	private final TaxiDriverRepository taxiDriverRepository;

	@Autowired
	public CustomTaxiDriverDetailService(TaxiDriverRepository taxiDriverRepository) {
		this.taxiDriverRepository = taxiDriverRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

		TaxiDriver taxiDriver = taxiDriverRepository.findByLoginId(loginId).orElseThrow(
			() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

		return new CustomTaxiDriverDetail(taxiDriver);
	}
}
