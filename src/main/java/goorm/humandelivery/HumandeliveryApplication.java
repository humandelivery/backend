package goorm.humandelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class HumandeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumandeliveryApplication.class, args);
	}

}
