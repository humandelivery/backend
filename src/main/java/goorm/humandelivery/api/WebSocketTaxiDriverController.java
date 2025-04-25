package goorm.humandelivery.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@MessageMapping("/taxi-driver")  // "/app/taxi-driver"
public class WebSocketTaxiDriverController {

	@MessageMapping("/hello")
	@SendTo("/topic/hello")
	public String hello(String message) {
		log.info("서버 hello() 진입: {} ", message);
		return message.toUpperCase();
	}

	@MessageMapping("/location")
	@SendTo("/topic/hello")
	public String location(String message) {
		return message;
	}
}
