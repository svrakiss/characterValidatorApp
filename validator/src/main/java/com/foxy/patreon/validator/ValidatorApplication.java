package com.foxy.patreon.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidatorApplication.class, args);
	}
	@Bean
	public WebClient rest(){
		return WebClient.builder()
		// .filter(new ServerBearerExchangeFilterFunction())
		.build();
	}

}
