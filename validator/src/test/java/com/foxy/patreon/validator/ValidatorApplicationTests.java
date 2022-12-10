package com.foxy.patreon.validator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"amazon.dynamodb.endpoint=http://dynamodb-local:8000/",
"amazon.aws.accesskey=test1",
"amazon.aws.secretkey=test213",
"amazon.aws.region=us-east-1"})
class ValidatorApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testCreation(){

	}

}
