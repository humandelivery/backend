package goorm.humandelivery.domain.model.entity;

public enum DrivingStatus {
	BEFORE_DRIVING("손님탑승전"),
	ON_DRIVING("손님탑승완료"),
	COMPLETE("운행완료");

	private final String description;

	DrivingStatus(String description) {
		this.description = description;
	}
}
