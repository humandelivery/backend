package goorm.humandelivery.domain.model.entity;

public enum RequestStatus {
	OK("완료"),
	FAILED("실패");

	private final String description;

	RequestStatus(String description) {
		this.description = description;
	}
}
