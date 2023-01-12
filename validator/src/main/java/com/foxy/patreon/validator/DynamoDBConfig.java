package com.foxy.patreon.validator;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.extensions.AutoGeneratedTimestampRecordExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile("test")
public class DynamoDBConfig {

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;
    @Value("${amazon.aws.region}")
    private String amazonAWSRegion;
    @Bean
    public DynamoDbClient amazonDynamoDB(AwsCredentials awsCredentials) throws URISyntaxException {
        
        var builder
          = DynamoDbClient.builder().region(Region.of(amazonAWSRegion)).endpointOverride(URI.create(amazonDynamoDBEndpoint))
          .credentialsProvider(StaticCredentialsProvider.create(awsCredentials));
        var amazonDynamoDB = builder.build();

        
        return amazonDynamoDB;
    }
@Bean
public DynamoDbEnhancedClient enhancedClient(DynamoDbClient amazonDynamoDB) throws URISyntaxException{
    return DynamoDbEnhancedClient.builder().dynamoDbClient(amazonDynamoDB)
    .extensions(AutoGeneratedTimestampRecordExtension.create())
    .build();
}
    @Bean
    public AwsCredentials amazonAWSCredentials() {
        return AwsBasicCredentials.create(
          amazonAWSAccessKey, amazonAWSSecretKey);
    }
}