package goorm.humandelivery.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.humandelivery.application.CustomerService;
import goorm.humandelivery.domain.model.request.CreateCustomerRequest;
import goorm.humandelivery.domain.model.response.CreateCustomerResponse;
import goorm.humandelivery.domain.model.request.LoginCustomerRequest;
import goorm.humandelivery.domain.model.response.LoginCustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

	private final CustomerService customerService;

	// 회원가입
	@PostMapping
	public ResponseEntity<CreateCustomerResponse> register(@RequestBody @Valid CreateCustomerRequest createCustomerRequest) {
		CreateCustomerResponse response = customerService.register(createCustomerRequest);

		return ResponseEntity.ok(response);
	}

	// 로그인
	@PostMapping("/auth-tokens")
	public ResponseEntity<LoginCustomerResponse> login(@RequestBody @Valid LoginCustomerRequest loginCustomerRequest) {
		log.info("customer login 호출");
		LoginCustomerResponse response = customerService.authenticateAndGenerateToken(loginCustomerRequest);

		return ResponseEntity.ok(response);
	}

	// 회원정보수정

	// 회원 탈퇴

}
