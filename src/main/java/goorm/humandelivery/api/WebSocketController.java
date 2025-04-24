package goorm.humandelivery.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	/**
	 *  config.setApplicationDestinationPrefixes("/app");
	 */

	@MessageMapping("/test")  //  /app/test
	public String test(String string) {
		return null;
	}



}
