package goorm.humandelivery.domain.model.response;

import goorm.humandelivery.domain.model.entity.TaxiType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaxiTypeResponse {

	private TaxiType taxiType;
}
