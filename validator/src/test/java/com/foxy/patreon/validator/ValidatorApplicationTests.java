package com.foxy.patreon.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
// import org.springframework.web.client.RestTemplate;

import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.service.ValidatorService;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;

@SpringBootTest
@TestPropertySource(properties = {"amazon.dynamodb.endpoint=http://dynamodb-local:8000/",
"amazon.aws.accesskey=test1",
"amazon.aws.secretkey=test213",
"amazon.aws.region=us-east-1"})
class ValidatorApplicationTests {
@Autowired
DynamoDbEnhancedClient enhancedClient;
@Autowired
DynamoDbClient client;
@Autowired
ValidatorService validatorService;
org.slf4j.Logger logger= LoggerFactory.getLogger(ValidatorApplicationTests.class);
	@Test
	void contextLoads() {
	}

	@ParameterizedTest()
	@ValueSource(strings ={"Patronnasda"})
	void testCreation(String name){

		try{
			DynamoDbTable<PatronEntity> table = enhancedClient.table(name,TableSchema.fromBean(PatronEntity.class));
			table.createTable();
			client.waiter().waitUntilTableExists(b-> b.tableName(name));
			logger.info("Nothing happened");
		}catch(Exception e){
			System.out.println("Something Happened");
			logger.error(e.getMessage());
		}finally{
			logger.info(client.describeTable(DescribeTableRequest.builder().tableName(name).build()).toString());
			client.deleteTable(DeleteTableRequest.builder()
			.tableName(name)
			.build());
			client.waiter().waitUntilTableNotExists(b-> b.tableName(name));
		}
	}
	@Test
	void testApi(){
		StringBuilder sb = new StringBuilder("http://coolwhip_2:65010/campaign/members");
		
		String token = "N1QhCFcmYF2eePRB4xi4qpISuUhwDE9s_Ix_rAG0VpQ";
		sb.append("?access_token="+token);
		sb.append("&campaign_id=9684285");
		// ResponseEntity responseEntity = new RestTemplate().getForEntity(sb.toString(), Object.class);
		// System.out.println(responseEntity);
	}
	@ParameterizedTest
	@ValueSource(strings={"Patron"})
	void testUpdateMembers(String tableName){
		if(!client.listTables(ListTablesRequest.builder().build()).tableNames().contains(tableName))
		{
			try{
				DynamoDbTable<PatronEntity> table = enhancedClient.table(tableName,TableSchema.fromBean(PatronEntity.class));
			table.createTable();
			client.waiter().waitUntilTableExists(b-> b.tableName(tableName));
			}catch(Exception e){
				System.out.println("Something Happened");
				logger.error(e.getMessage());
			}finally{
				logger.info(client.describeTable(DescribeTableRequest.builder().tableName(tableName).build()).toString());
			
			}
		}
		
		try{validatorService.updateMembers("9684285");}
		catch(Exception e){
			System.out.println("Something Happened");
			logger.error(e.getMessage());
		}finally{
			logger.info(client.describeTable(DescribeTableRequest.builder().tableName(tableName).build()).toString());
		
		}
	}
	@Test
	void findById(){
		PatronEntity p = new PatronEntity();
		// p.setDiscordId(null);
		p.setId("PATREON_01a79487-8d90-4fc7-b783-9238ef0e490c");
		logger.info(validatorService.findById(p)
		.block().toString());
	}
}

