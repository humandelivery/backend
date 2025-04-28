package goorm.humandelivery.domain.model.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {

	@NotBlank
	private double latitude;   // 위도

	@NotBlank
	private double longitude;  // 경도
}
