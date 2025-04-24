package goorm.humandelivery.domain.model.request;

import jakarta.validation.constraints.NotBlank;

public class CreateTaxiDriverRequest {

	@NotBlank
	private String loginId;

	@NotBlank
	private String password;
}
