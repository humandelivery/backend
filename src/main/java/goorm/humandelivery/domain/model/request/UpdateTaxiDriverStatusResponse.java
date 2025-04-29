package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 택시 상태변경
 */
@Getter
@Setter
public class UpdateTaxiDriverStatusResponse {

	// 요청 결과 이후 택시 상태
	TaxiDriverStatus taxiDriverStatus;

}
