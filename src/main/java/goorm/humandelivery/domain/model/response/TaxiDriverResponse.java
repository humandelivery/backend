package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.Taxi;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxiDriverResponse {

	private Long id;
	private Taxi taxi;
	private String loginId;
	private String password;
	private String name;
	private String licenseCode;
	private String phoneNumber;

	private TaxiDriverResponse(Long id, Taxi taxi, String loginId, String password, String name, String licenseCode,
		String phoneNumber) {
		this.id = id;
		this.taxi = taxi;
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.licenseCode = licenseCode;
		this.phoneNumber = phoneNumber;
	}

	public static TaxiDriverResponse from(TaxiDriver taxiDriver) {
		return new TaxiDriverResponse(
			taxiDriver.getId(),
			taxiDriver.getTaxi(),
			taxiDriver.getLoginId(),
			taxiDriver.getPassword(),
			taxiDriver.getName(),
			taxiDriver.getLicenseCode(),
			taxiDriver.getPhoneNumber()
		);
	}
}
