package goorm.humandelivery.domain.model.entity;

public enum DrivingStatus {
	BEFORE_DRIVING("운행전"),
	ON_DRIVING("운행중"),
	COMPLETE("운행완료");

	private final String description;

	DrivingStatus(String description) {
		this.description = description;
	}
}
