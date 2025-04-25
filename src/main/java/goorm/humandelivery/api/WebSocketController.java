package goorm.humandelivery.api;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebSocketController {

	/**
	 *  config.setApplicationDestinationPrefixes("/app");
	 */

	@MessageMapping("/test")  //  /app/test
	public String test(Principal principal, String string) {
		principal.getName();


		log.info(string);
		return null;
	}



}
