package goorm.humandelivery.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.common.security.jwt.JwtUtil;
import goorm.humandelivery.domain.model.request.CreateTaxiDriverRequest;
import goorm.humandelivery.domain.model.request.LoginTaxiDriverRequest;
import goorm.humandelivery.domain.model.response.JwtResponse;
import goorm.humandelivery.domain.model.response.TaxiDriverResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/taxi-driver")
public class TaxiDriverController {

	private final TaxiDriverService taxiDriverService;
	private final JwtUtil jwtUtil;

	@Autowired
	public TaxiDriverController(TaxiDriverService taxiDriverService, JwtUtil jwtUtil) {
		this.taxiDriverService = taxiDriverService;
		this.jwtUtil = jwtUtil;
	}

	// 회원가입
	@PostMapping
	public ResponseEntity<TaxiDriverResponse> register(@RequestBody @Valid CreateTaxiDriverRequest taxiDriverRequest) {

		TaxiDriverResponse response = taxiDriverService.register(taxiDriverRequest);

		return ResponseEntity.ok(response);
	}

	// 로그인
	@PostMapping("/auth-tokens")
	public ResponseEntity<?> loginTaxiDriver(@RequestBody @Valid LoginTaxiDriverRequest loginTaxiDriverRequest) {
		// 아이디 찾고.. 패스워드 암호화 후 일치하는 지 확인해야 함.. 서비스에서 해야겠네

		taxiDriverService.validate(loginTaxiDriverRequest);

		// 있으면? -> 토큰 만들어서 돌려줌
		String token = jwtUtil.generateToken(loginTaxiDriverRequest.getLoginId());

		return ResponseEntity.ok(new JwtResponse(token));
	}
}
