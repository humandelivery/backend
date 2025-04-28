package goorm.humandelivery.domain.model.request;

import goorm.humandelivery.common.util.annotation.ValidEnum;
import goorm.humandelivery.domain.model.entity.RequestStatus;
import goorm.humandelivery.domain.model.entity.TaxiDriverStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 택시 상태변경
 */
@Getter
@Setter
public class UpdateTaxiDriverStatusResponse {

	// 요청 결과 -> 성공 실패 응답용도
	RequestStatus requestStatus;


	TaxiDriverStatus taxiDriverStatus;

}
