package goorm.humandelivery.domain.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCustomerRequest {

	@NotBlank(message = "아이디를 입력해 주세요.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력해 주세요.")
	private String password;

}
