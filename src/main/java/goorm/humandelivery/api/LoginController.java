package goorm.humandelivery.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import goorm.humandelivery.application.CustomerService;
import goorm.humandelivery.application.TaxiDriverService;
import goorm.humandelivery.domain.model.request.CreateTaxiDriverRequest;
import goorm.humandelivery.domain.model.request.LoginTaxiDriverRequest;

@Controller
@RequestMapping("/login")
public class LoginController {

	private final CustomerService customerService;
	private final TaxiDriverService taxiDriverService;

	@Autowired
	public LoginController(CustomerService customerService, TaxiDriverService taxiDriverService) {
		this.customerService = customerService;
		this.taxiDriverService = taxiDriverService;
	}
}
