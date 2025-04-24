package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.Taxi;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxiDriverResponse {

	private String loginId;
	private String name;
	private String licenseCode;
	private String phoneNumber;
	private Taxi taxi;

	private TaxiDriverResponse(Taxi taxi, String loginId, String name, String licenseCode,
		String phoneNumber) {
		this.taxi = taxi;
		this.loginId = loginId;
		this.name = name;
		this.licenseCode = licenseCode;
		this.phoneNumber = phoneNumber;
	}

	public static TaxiDriverResponse from(TaxiDriver taxiDriver) {
		return new TaxiDriverResponse(
			taxiDriver.getTaxi(),
			taxiDriver.getLoginId(),
			taxiDriver.getName(),
			taxiDriver.getLicenseCode(),
			taxiDriver.getPhoneNumber()
		);
	}
}
