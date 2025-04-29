package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Location {

	@NotBlank
	private Double latitude;   // 위도

	@NotBlank
	private Double longitude;  // 경도
}
