package goorm.humandelivery.domain.model.entity;

public enum FuelType {
	GASOLINE("가솔린"),
	DIESEL("디젤"),
	LNG("LNG"),
	ELECTRIC("전기");

	private final String description;

	FuelType(String description) {
		this.description = description;
	}
}
