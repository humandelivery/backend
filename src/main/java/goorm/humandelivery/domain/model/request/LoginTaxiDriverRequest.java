package goorm.humandelivery.domain.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginTaxiDriverRequest {

	@Email
	@NotBlank
	private String loginId;


	@NotBlank
	private String password;
}
