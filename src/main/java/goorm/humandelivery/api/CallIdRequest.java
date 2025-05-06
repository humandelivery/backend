package goorm.humandelivery.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallIdRequest {

	@NotBlank
	private Long callId;
}
