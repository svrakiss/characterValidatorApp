package com.foxy.patreon.validator.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class OAuth2ClientController {

	@GetMapping("/")
	public Mono<String> index(@RegisteredOAuth2AuthorizedClient(registrationId = "patreon") OAuth2AuthorizedClient authorizedClient) {
		return Mono.just(authorizedClient.getAccessToken())
				.thenReturn("index");
	}
    @RequestMapping(value="/token", method={RequestMethod.GET,RequestMethod.POST})
    public String requestMethodName(@RegisteredOAuth2AuthorizedClient(registrationId = "patreon") OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getAccessToken().toString();
    }
    
 
}