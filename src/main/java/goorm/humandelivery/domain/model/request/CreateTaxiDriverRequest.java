package goorm.humandelivery.domain.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaxiDriverRequest {

	@Email
	@NotBlank
	private String loginId;

	@NotBlank
	private String password;

	@NotBlank
	private String name;

	@NotBlank
	private String licenseCode;

	@NotBlank
	private String phoneNumber;

	@Valid
	private CreateTaxiRequest taxi;


}
