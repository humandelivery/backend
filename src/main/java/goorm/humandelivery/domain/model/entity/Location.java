package goorm.humandelivery.domain.model.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {

	@NotBlank
	private Double latitude;   // 위도

	@NotBlank
	private Double longitude;  // 경도
}
