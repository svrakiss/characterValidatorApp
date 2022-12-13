package com.foxy.patreon.validator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2LoginSpec;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
// import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
// import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class OAuth2ClientSecurityConfig {
  
	@Bean
    // @Autowired
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository, ReactiveOAuth2AuthorizedClientService authorizedClientService) {
		http
		.oauth2Login(oauth2 -> oauth2
				// .authenticationConverter(this.authenticationConverter())
				// .authenticationMatcher(this.authenticationMatcher())
				// .authenticationManager(this.authenticationManager())
				// .authenticationSuccessHandler(this.authenticationSuccessHandler())
				// .authenticationFailureHandler(this.authenticationFailureHandler())
				.clientRegistrationRepository(clientRegistrationRepository)
				// .authorizedClientRepository(authorizedClientRepository)
				.authorizedClientService(authorizedClientService)
				// .authorizationRequestResolver(this.authorizationRequestResolver())
				// .authorizationRequestRepository(this.authorizationRequestRepository())
				// .securityContextRepository(this.securityContextRepository())
			);
		return http.build();
	}

    // @Autowired
    // private ServerAuthenticationFailureHandler authenticationFailureHandler() {
    //     return null;
    // }
    // private ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
    //     return null;
    // }
    // private ReactiveClientRegistrationRepository clientRegistrationRepository() {
    //     return null;
    // }
    // private ServerSecurityContextRepository securityContextRepository() {
    //     return null;
    // }
    // private ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
    //     return null;
    // }
    // private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver() {
    //     return null;
    // }
    @Bean
     ReactiveOAuth2AuthorizedClientService authorizedClientService(ReactiveClientRegistrationRepository clientRegistrations) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
    }
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
                .userInfoUri(redirectUri)
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
WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
@Value("${spring.security.oauth2.client.token}") String token) {
	ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
			new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
	oauth2Client.setDefaultClientRegistrationId("patreon");
    ;
    return WebClient.builder().defaultHeader("Authorization", "Bearer "+token) 
			.filter(oauth2Client)
			.build();
}
@Bean
ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrations, ReactiveOAuth2AuthorizedClientService clientService)
{
    return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, clientService);
    
   
}
}