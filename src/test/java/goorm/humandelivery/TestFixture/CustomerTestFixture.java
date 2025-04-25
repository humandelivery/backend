package goorm.humandelivery.TestFixture;

import goorm.humandelivery.domain.model.entity.Customer;
import goorm.humandelivery.domain.model.request.CreateCustomerRequest;
import goorm.humandelivery.domain.model.request.LoginCustomerRequest;
import goorm.humandelivery.domain.model.response.CreateCustomerResponse;
import goorm.humandelivery.domain.model.response.LoginCustomerResponse;

public class CustomerTestFixture {

	public static Customer createCustomerEntity(String loginId, String password, String name, String phoneNumber) {
		return Customer.builder()
			.loginId(loginId)
			.password(password)
			.name(name)
			.phoneNumber(phoneNumber)
			.build();
	}

	public static CreateCustomerRequest createCreateCustomerRequest(String loginId, String password, String name, String phoneNumber) {
		return new CreateCustomerRequest(loginId, password, name, phoneNumber);
	}
	public static LoginCustomerRequest createLoginCustomerRequest(String loginId, String password) {
		return new LoginCustomerRequest(loginId, password);
	}
	public static LoginCustomerResponse createLoginCustomerResponse(String accessToken) {
		return new LoginCustomerResponse(accessToken);
	}
}
