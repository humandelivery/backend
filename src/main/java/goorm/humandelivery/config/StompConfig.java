package goorm.humandelivery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import goorm.humandelivery.common.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompConfig implements WebSocketMessageBrokerConfigurer {

	private final JwtUtil jwtUtil;

	@Autowired
	public StompConfig(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// 인바운드 채널
		// 클라이언트 -> 웹소켓 서버로 보내는 통로.
		registration.interceptors(new ChannelInterceptor() {

			// ChannelInterceptor 이놈이 그 메세지를 가로채서, 무언가 할 수 있게 해준다.
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor =
					MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				log.info("💡 WebSocket CONNECT Authorization: {}", message);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String token = accessor.getFirstNativeHeader("Authorization");

					// 1. 인증 로직 수행 (예: JWT 검증)
					boolean isValid = jwtUtil.validateToken(token);

					if (!isValid) {
						// 발생한 예외는 STOMP 클라이언트에게 ERROR 프레임으로 반환됨 -> 클라이언트로..
						throw new IllegalArgumentException("Invalid JWT Token");
					}

					// 2. 토큰으로부터 Authentication 객체 생성.
					// SecurityContext 에 등록할 필요 없음.
					Authentication authentication = jwtUtil.getAuthentication(token);


					// 3. accessor 에 authentication 객체 세팅
					// @MessageMapping 메서드가 포함된 컨트롤러에서 @Principal 어노테이션으로 정보 추출 가능.
					accessor.setUser(authentication);
				}

				return message;
			}
		});
	}

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
