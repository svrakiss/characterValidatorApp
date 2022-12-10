package com.foxy.patreon.validator;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

@Configuration
@EnableDynamoDBRepositories
  (basePackages = "com.foxy.patreon.validator.repository")
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
    public AmazonDynamoDB amazonDynamoDB() {
        // AmazonDynamoDB amazonDynamoDB 
        var builder
          = AmazonDynamoDBClientBuilder.standard();
          builder.setRegion(amazonAWSRegion);
        //   builder.setCredentials(()->amazonAWSCredentials());
        //   (amazonAWSCredentials());
        var amazonDynamoDB = builder.build();
        if (!StringUtils.hasText(amazonDynamoDBEndpoint)) {
            amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
        }
        
        return amazonDynamoDB;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(
          amazonAWSAccessKey, amazonAWSSecretKey);
    }
}