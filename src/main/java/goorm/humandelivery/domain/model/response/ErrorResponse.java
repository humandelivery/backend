package goorm.humandelivery.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

import lombok.NoArgsConstructor;



@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class ErrorResponse {

	private String code;

	private String message;

}
