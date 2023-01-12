package com.foxy.patreon.validator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
// import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
// import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
// import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
// import org.springframework.security.oauth2.client.registration.ClientRegistration;
// import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
// import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
// import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
// import org.springframework.security.oauth2.core.AuthorizationGrantType;
// import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxy.patreon.validator.entity.PatronEntity;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
// @EnableWebFluxSecurity
public class OAuth2ClientSecurityConfig {
  @Autowired
  private SsmClient ssmClient;
	// @Bean
    // // @Autowired
	// public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository, ReactiveOAuth2AuthorizedClientService authorizedClientService) {
	// 	// http
	// 	// .oauth2Login(oauth2 -> oauth2
	// 	// 		.clientRegistrationRepository(clientRegistrationRepository)
	// 	// 		.authorizedClientService(authorizedClientService)
	// 	// 	);
	// 	return http.build();
	// }
    // // @Bean
    // // public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
    // //     return http.build();
	// // }
    // // }
    // @Bean
    //  ReactiveOAuth2AuthorizedClientService authorizedClientService(ReactiveClientRegistrationRepository clientRegistrations) {
    //     return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
    // }
    // @Bean
    // ReactiveClientRegistrationRepository clientRegistrations(
    //     @Value("${spring.security.oauth2.client.provider.patreon.authorization-uri}") String authorizationUri,
    //     @Value("${spring.security.oauth2.client.provider.patreon.token-uri}") String token_uri,
    //         @Value("${spring.security.oauth2.client.registration.patreon.client-id}") String client_id,
    //         @Value("${spring.security.oauth2.client.registration.patreon.client-secret}") String client_secret,
    //         @Value("${spring.security.oauth2.client.registration.patreon.scope}") Set<String> scope,
    //         @Value("${spring.security.oauth2.client.registration.patreon.authorization-grant-type}") String authorizationGrantType,
    //         @Value("${spring.security.oauth2.client.registration.patreon.redirect-uri}") String redirectUri
    // ) {
    //     System.out.println(redirectUri);
    //     var intermediate= ClientRegistration
    //             .withRegistrationId("patreon")
    //             .authorizationUri(authorizationUri)
    //             .tokenUri(token_uri)
    //             .clientId(client_id)
    //             .clientSecret(client_secret)
    //             .redirectUri(redirectUri)
    //             .scope(scope)
    //             .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
    //             .userInfoUri(redirectUri)
    //             .build();
    //             return new InMemoryReactiveClientRegistrationRepository(intermediate);
    // }

//     @Bean
//     @Autowired
// WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
// @Value("${spring.security.oauth2.client.token}") String token) {
// 	ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
// 			new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
// 	oauth2Client.setDefaultClientRegistrationId("patreon");
//     ;
//     return WebClient.builder().defaultHeader("Authorization", "Bearer "+token) 
// 			.filter(oauth2Client)
// 			.build();
// }
// @Bean
// ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrations, ReactiveOAuth2AuthorizedClientService clientService)
// {
//     return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, clientService);
    
   
// }
@Bean
  public WebClient webClient(
      @Value("${http.client.connection-timeout-millis}") final int connectionTimeoutMillis,
      @Value("${http.client.socket-timeout-millis}") final int socketTimeoutMillis,
      @Value("${http.client.wire-tap-enabled}") final boolean wireTapEnabled,
      final ObjectMapper objectMapper) {
        String token = ssmClient
        .getParameter(request->request.name("/config/ValidatorMS-production/spring.security.oauth2.client.token").withDecryption(true))
            .parameter()
            .value();
    Consumer<Connection> doOnConnectedConsumer = connection ->
        connection
            .addHandlerFirst(new ReadTimeoutHandler(socketTimeoutMillis, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(connectionTimeoutMillis, TimeUnit.MILLISECONDS));

    // TcpClient tcpClient = TcpClient.newConnection()
    //     .wiretap(wireTapEnabled)
    //     .option(CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
    //     .doOnConnected(doOnConnectedConsumer);

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
        .doOnConnected(doOnConnectedConsumer)))
        .defaultHeader("Authorization", "Bearer "+token) 
        // .exchangeStrategies(customExchangeStrategies(objectMapper))
        .build();
}
@Bean
public DynamoDbTable<PatronEntity> getTable(DynamoDbEnhancedClient client){
    String tableName = ssmClient.getParameter(a->a.name("/config/ValidatorMS-production/spring.cloud.aws.dynamodb.tableName"))
    .parameter()
    .value();
    return client.table(tableName, TableSchema.fromBean(PatronEntity.class));
}

}