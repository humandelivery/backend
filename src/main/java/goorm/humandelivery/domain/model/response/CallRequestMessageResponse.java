package goorm.humandelivery.domain.model.response;

public class CallRequestMessageResponse {
	private String message;
	// 성공 실패 여부 Enum으로 추가

	public CallRequestMessageResponse(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
