package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {

	@NotNull
	private Double latitude;   // 위도

	@NotNull
	private Double longitude;  // 경도
}
