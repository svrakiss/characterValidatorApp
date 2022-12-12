package com.foxy.patreon.validator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2LoginSpec;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
// import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
// import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class OAuth2ClientSecurityConfig {
  
	// @Bean
    // @Autowired
	// public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager) {
	// 	http
	// 		// .oauth2Client(oauth2 -> oauth2
	// 		// 	.authorizationRequestRepository(this.authorizationRequestRepository())
				
	// 		// );

    //         // .authorizeExchange(authorize -> authorize
    //         // .anyExchange().authenticated()
    //         // )
    //         .oauth2Login().
    //         clientRegistrationRepository(reactiveOAuth2AuthorizedClientManager.authorize(null))
    //         .and()
    //         // .and()
    //         .csrf(csrf-> csrf.disable());
	// 	return http.build();
	// }
    @Bean
    ReactiveClientRegistrationRepository clientRegistrations(
        @Value("${spring.security.oauth2.client.provider.patreon.authorization-uri}") String authorizationUri,
        @Value("${spring.security.oauth2.client.provider.patreon.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.patreon.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.patreon.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.patreon.scope}") Set<String> scope,
            @Value("${spring.security.oauth2.client.registration.patreon.authorization-grant-type}") String authorizationGrantType,
            @Value("${spring.security.oauth2.client.registration.patreon.redirect-uri}") String redirectUri
    ) {
        System.out.println(redirectUri);
        var intermediate= ClientRegistration
                .withRegistrationId("patreon")
                .authorizationUri(authorizationUri)
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .redirectUri(redirectUri)
                .scope(scope)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
                return new InMemoryReactiveClientRegistrationRepository(intermediate);
    }
    // @Bean
	// public WebClient rest(){
	// 	return WebClient.builder()
	// 	// .filter(new ServerBearerExchangeFilterFunction())
	// 	.build();
	// }
    @Bean
    @Autowired
WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
	ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
			new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
	oauth2Client.setDefaultClientRegistrationId("patreon");

    return WebClient.builder()
			.filter(oauth2Client)
			.build();
}
@Bean
ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrations, ServerOAuth2AuthorizedClientRepository authorizedClientRepository)
{
    InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);

    return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, clientService);
}
}