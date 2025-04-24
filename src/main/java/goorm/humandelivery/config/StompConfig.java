package goorm.humandelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 연결할 WebSocket 핸드쉐이크용 HTTP URL
		// 인증, 콜 요청
		registry.addEndpoint("/ws").withSockJS();
	}


	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// setApplicationDestinationPrefixes 메세지는 컨트롤러의 @MessageMapping 메서드로 라우팅
		config.setApplicationDestinationPrefixes("/app");

		/**
		 * 	simpleBroker(내장브로커) 사용 -> 추후 외부 브로커 시스템으로 변경(튜닝 포인트)
		 * 	/topic : 관례상 pup/sub 구조에서 사용
		 * 	/queue : 관례상 일대일 메세지 전송에서 사용.
		 */
		config.enableSimpleBroker("/topic", "/queue");
	}
}
