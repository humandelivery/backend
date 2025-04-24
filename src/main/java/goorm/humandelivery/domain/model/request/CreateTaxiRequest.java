package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.common.util.annotation.ValidEnum;
import goorm.humandelivery.domain.model.entity.FuelType;
import goorm.humandelivery.domain.model.entity.TaxiType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaxiRequest {

	private String model;

	@ValidEnum(enumClass = TaxiType.class, message = "지원하지 않는 연료 타입입니다.")
    private String taxiType;

	private String plateNumber;

	@ValidEnum(enumClass = FuelType.class, message = "지원하지 않는 연료 타입입니다.")
	private String fuelType;
}
