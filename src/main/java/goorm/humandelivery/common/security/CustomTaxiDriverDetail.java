package goorm.humandelivery.common.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import goorm.humandelivery.domain.model.entity.TaxiDriver;

public class CustomTaxiDriverDetail implements UserDetails {

	private TaxiDriver taxiDriver;

	public CustomTaxiDriverDetail(TaxiDriver taxiDriver) {
		this.taxiDriver = taxiDriver;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 권한인데? 우린 지금 권한이 없어서 패스.
		return List.of();
	}

	@Override
	public String getPassword() {
		return taxiDriver.getPassword();
	}

	@Override
	public String getUsername() {
		return taxiDriver.getLoginId();
	}
}
