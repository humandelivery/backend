package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {

	@NotBlank
	private Double latitude;   // 위도

	@NotBlank
	private Double longitude;  // 경도
}
