package goorm.humandelivery.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.domain.model.request.CreateTaxiDriverRequest;
import goorm.humandelivery.domain.model.response.TaxiDriverResponse;

@Controller
@RequestMapping("/api/v1/taxi-driver")
public class TaxiDriverController {

	private final TaxiDriverService taxiDriverService;

	@Autowired
	public TaxiDriverController(TaxiDriverService taxiDriverService) {
		this.taxiDriverService = taxiDriverService;
	}


	// 회원가입
	@PostMapping
	public ResponseEntity<TaxiDriverResponse> register(@RequestBody CreateTaxiDriverRequest taxiDriverRequest) {

		taxiDriverService.register(taxiDriverRequest);

		return null;
	}



}
