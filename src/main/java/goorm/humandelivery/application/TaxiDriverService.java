package goorm.humandelivery.application;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.humandelivery.common.exception.IncorrectPasswordException;
import goorm.humandelivery.domain.model.entity.FuelType;
import goorm.humandelivery.domain.model.entity.Taxi;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import goorm.humandelivery.domain.model.entity.TaxiType;
import goorm.humandelivery.domain.model.request.CreateTaxiDriverRequest;
import goorm.humandelivery.domain.model.request.CreateTaxiRequest;
import goorm.humandelivery.domain.model.request.LoginTaxiDriverRequest;
import goorm.humandelivery.domain.model.response.TaxiDriverResponse;
import goorm.humandelivery.domain.repository.TaxiDriverRepository;
import goorm.humandelivery.domain.repository.TaxiRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class TaxiDriverService {

	private final TaxiDriverRepository taxiDriverRepository;
	private final TaxiRepository taxiRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public TaxiDriverService(TaxiDriverRepository taxiDriverRepository, TaxiRepository taxiRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.taxiDriverRepository = taxiDriverRepository;
		this.taxiRepository = taxiRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Transactional
	public TaxiDriverResponse register(CreateTaxiDriverRequest request) {

		// 택시 만들기
		Taxi savedTaxi = createTaxi(request);

		// 택시기사 만들기
		TaxiDriver savedTaxiDriver = createTaxiDriver(request, savedTaxi);

		// 엔티티는 DTO 를 몰라야 한다.
		return TaxiDriverResponse.from(savedTaxiDriver);
	}

	public void validate(LoginTaxiDriverRequest loginTaxiDriverRequest) {

		TaxiDriver taxiDriver = taxiDriverRepository.findByLoginId(loginTaxiDriverRequest.getLoginId())
			.orElseThrow(
				() -> new EntityNotFoundException("아이디에 해당하는 택시기사가 존재하지 않습니다.")
			);

		// 패스워드 검증
		boolean isSamePassword = taxiDriver.isSamePassword(loginTaxiDriverRequest.getPassword(), bCryptPasswordEncoder);


		if (!isSamePassword) {
			throw new IncorrectPasswordException("패스워드가 올바르지 않습니다.");
		}
	}

	private Taxi createTaxi(CreateTaxiDriverRequest request) {
		CreateTaxiRequest createTaxiRequest = request.getTaxi();

		Taxi taxi = Taxi.builder()
			.fuelType(FuelType.valueOf(createTaxiRequest.getFuelType()))
			.taxiType(TaxiType.valueOf(createTaxiRequest.getTaxiType()))
			.plateNumber(createTaxiRequest.getPlateNumber())
			.model(createTaxiRequest.getModel())
			.build();

		return taxiRepository.save(taxi);
	}

	private TaxiDriver createTaxiDriver(CreateTaxiDriverRequest request, Taxi savedTaxi) {
		String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

		boolean isExist = taxiDriverRepository.existsByLoginId(request.getLoginId());

		if (isExist) {
			throw new EntityExistsException("이미 존재하는 택시기사 아이디입니다.");
		}

		TaxiDriver taxiDriver = TaxiDriver.builder()
			.taxi(savedTaxi)
			.loginId(request.getLoginId())
			.password(encodedPassword)
			.name(request.getName())
			.licenseCode(request.getLicenseCode())
			.phoneNumber(request.getPhoneNumber())
			.build();

		// DB에 저장
		return taxiDriverRepository.save(taxiDriver);
	}
}
