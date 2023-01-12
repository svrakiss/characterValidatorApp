package com.foxy.patreon.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
// import org.springframework.web.client.RestTemplate;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foxy.patreon.validator.entity.PatronEntity;
import com.foxy.patreon.validator.repository.TableExtension;
import com.foxy.patreon.validator.service.ValidatorService;
import reactor.test.StepVerifier;
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

static ObjectMapper objectMapper = JsonMapper.builder()
.addModule(new JavaTimeModule())
.build();
@Autowired
ValidatorService validatorService;
org.slf4j.Logger logger= LoggerFactory.getLogger(ValidatorApplicationTests.class);
	@Test
	void contextLoads() {
		var table = enhancedClient.table("Patron", TableSchema.fromBean(PatronEntity.class));
		logger.info(table.describeTable().toString());
		var result =TableSchema.fromBean(PatronEntity.class);
		logger.info(result.attributeNames().toString());
		result.tableMetadata().indices().stream()
		.map(a->a.name())
		.forEach(logger::info);
		table.tableSchema().tableMetadata().indices().stream()
		.map(a->a.name())
		.map(a->table.index(a))
		.map(n->n.indexName())
		.forEach(logger::info);
	}

	@ParameterizedTest()
	@ValueSource(strings ={"Patron"})
	void testCreation(String name){

		try{
			DynamoDbTable<PatronEntity> table = enhancedClient.table(name,TableSchema.fromBean(PatronEntity.class));
			// table.createTable();
			TableExtension.createTableWithIndices(table);
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
	@MethodSource("testUpdateMembersArgSource")
	void testUpdateMembers(String tableName,Integer pageSize){
		if(!client.listTables(ListTablesRequest.builder().build()).tableNames().contains(tableName))
		{
			try{
				DynamoDbTable<PatronEntity> table = enhancedClient.table(tableName,TableSchema.fromBean(PatronEntity.class));
				TableExtension.createTableWithIndices(table);
			client.waiter().waitUntilTableExists(b-> b.tableName(tableName));
			}catch(Exception e){
				System.out.println("Something Happened");
				logger.error(e.getMessage());
			}finally{
				logger.info(client.describeTable(DescribeTableRequest.builder().tableName(tableName).build()).toString());
			
			}
		}
		
		try{StepVerifier.create(
			validatorService.updateMembers("3793891",pageSize))
			// .consumeNextWith(p->logger.info(p.toString()))
			.expectNextCount(3551)
			.expectComplete()
			.verify();}
		catch(Exception e){
			System.out.println("Something Happened");
			logger.error(e.getMessage(),e);
		}finally{
			logger.info(client.describeTable(DescribeTableRequest.builder().tableName(tableName).build()).toString());
		
		}
	}
	@ParameterizedTest
	@MethodSource(value = "findByIdMethodSource")
	void findById(Map<String,String> args){
		PatronEntity p = new PatronEntity();
		p = parseArgs(p,args);
		validatorService.findById(p)
		.subscribe(a->logger.info(a.toString()));
	}
	
	public static Stream<Map<String,String>> findByIdMethodSource(){
		return Stream.of(Map.of("id","PATREON_01a79487-8d90-4fc7-b783-9238ef0e490c"),
		Map.of("patronid","01a79487-8d90-4fc7-b783-9238ef0e490c"),
		Map.of("discordid","536343876378820628"));
	}
	public static Stream<Arguments> testUpdateMembersArgSource() {
		return Stream.of(
			Arguments.of("Patron",501),
			Arguments.of("Patron",124)
		);
	}
	private static PatronEntity parseArgs(PatronEntity p, Map<String,String> args) {
		args.entrySet().forEach((key)->{switch (key.getKey().toLowerCase()) {
			case "patronid": p.setPatronId(key.getValue());
				break;
			case "status": p.setStatus(key.getValue());
				break;
			case "id": p.setId(key.getValue());
				break;
			case "discordid": p.setDiscordId(key.getValue());
				break;
			case "characterName": p.setCharacterName(key.getValue());
				break;
			default:
				break;
		}});
		return p;
	}
	@ParameterizedTest
	@MethodSource("testCharacterAddSource")
	void testCharacterAdd(PatronEntity patronEntity){
		StepVerifier.create(validatorService.addCharacter(patronEntity))
		.consumeNextWith(System.out::println)
		.expectComplete()
		.verify();
	}
	public static Stream<Arguments> testCharacterAddSource() throws StreamReadException, DatabindException, FileNotFoundException, IOException{
		File f = ResourceUtils.getFile("src/main/resources/example.json");
		List<PatronEntity> p = objectMapper
		.readValue(f,objectMapper
		.getTypeFactory().constructCollectionLikeType(List.class, PatronEntity.class));
		return p.stream()
		.map(Arguments::of);
	}
}

