package goorm.humandelivery.common.util.annotation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

	private List<String> acceptedValues;

	@Override
	public void initialize(ValidEnum annotation) {
		acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
			.map(Enum::name)
			.toList();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && acceptedValues.contains(value);
	}
}
