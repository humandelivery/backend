package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateTaxiDriverRequest {

	@NotBlank
	private String loginId;

	@NotBlank
	private String password;

}
