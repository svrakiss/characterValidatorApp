package com.myorg;

import software.constructs.Construct;

import java.util.Collections;
import java.util.List;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.SpringBootApplicationStack;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.BundlingOutput;
import software.amazon.awscdk.DockerBuildOptions;
import software.amazon.awscdk.DockerImage;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.codecommit.Code;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.CfnTable;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableAttributes;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.iam.IPrincipal;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.ssm.IStringParameter;
import software.amazon.awscdk.services.ssm.StringParameter;

public class InfraStack extends Stack {
    DockerImageAsset dockerImage;
    ITable dynamoTable;
        public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "InfraQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();

        // List<String>  packagingInstructions = List.of(
        //     "/bin/sh",
        //     "-c",
        //     "cd validator"+
        //     "&& mvn -DskipTests clean install"+
        //     "&& cp /asset-input/validator/target/validator.jar /asset-output/"
        // );
        // BundlingOptions options = BundlingOptions.builder()
        // .command(packagingInstructions)
        // .image(Runtime.JAVA_11.getBundlingImage())
        // .volumes(
        //     Collections.singletonList(
        //         DockerVolume.builder()
        //         .hostPath(System.getProperty("user.home")+"/.m2/")
        //         .containerPath("/root/.m2/")
        //         .build()
        //     )
        // ).user("root")
        // .outputType(BundlingOutput.ARCHIVED)
        // .entrypoint(List.of(
        //     "java",
        //     "-jar",
        //     "validator.jar"
        // ))
        // .build();
        
        dockerImage = DockerImageAsset.Builder
        .create(this, getStackName()+"Image")
        .directory("..//validator//")
        .build();
        dynamoTable =Table.fromTableName(this, getStackName()+"ProxyTable", "Patron");
        
        // Table dynamoTable2 = Table.Builder.create(this, getStackName()+"PatronTable")
        // .partitionKey(Attribute.builder()
        // .name("id")
        // .type(AttributeType.STRING)
        // .build())
        // .billingMode(BillingMode.PAY_PER_REQUEST)
        // .removalPolicy(RemovalPolicy.DESTROY)
        // .build();
        final IStringParameter tableNameParameter = StringParameter.Builder
        .create(this, getStackName()+"DynamoEndpoint")
        .parameterName("/config/ValidatorMS-production/spring.cloud.aws.dynamodb.tableName")
        .stringValue(dynamoTable.getTableName())
        .build();
        //  endpointUrl.
        // Repository springRepo = Repository.Builder.create(this, getStackName()+"ECRRepo")
        // .repositoryName(getStackName()+"ValidatorRepo")
        // .build();
        // System.out.println(dockerImage.getImage());
        // letSpringDoAnything = Role.Builder.create(this, getStackName()+"SpringGod")
        // .assumedBy(new ServicePrincipal("ecs.amazonaws.com"))
        // .build();
        // dynamoTable.grantFullAccess(letSpringDoAnything);
    }
}
