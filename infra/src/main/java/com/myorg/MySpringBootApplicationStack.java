package com.myorg;

import java.util.Collections;
import java.util.List;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Service;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.PrincipalBase;
import software.amazon.awscdk.services.iam.Role;
import software.constructs.Construct;

public class MySpringBootApplicationStack  extends Stack{ 
    Network network;
    public MySpringBootApplicationStack(
        final Construct scope,
        final String id,
        final Environment environment,
        final String dockerImageUrl,String tableArn) {
        super(scope, id, StackProps.builder()
          .stackName("SpringBootApplication")
          .env(environment)
          .build());
    
    
         network = new Network(this, "network", environment, "prod", new Network.NetworkInputParameters());
        Service service = new Service(this, "Service", environment, new ApplicationEnvironment("SpringBootApplication", "prod"),
          new Service.ServiceInputParameters(
            new Service.DockerImageSource(dockerImageUrl),
            Collections.emptyList(),
            Collections.emptyMap())
            .withContainerPort(5401)
            .withTaskRolePolicyStatements(List.of(
                PolicyStatement.Builder
                .create()
                .effect(Effect.ALLOW)
                .resources(List.of(tableArn))
                .actions(List.of("dynamodb:*"
                ))
                .build(),
                PolicyStatement.Builder
                .create()
                .effect(Effect.ALLOW)
                .actions(List.of(
                    "ssm:GetParametersByPath",
                    "ssm:GetParameters",
                    "ssm:GetParameter",
                    "ssm:DescribeParameters"
                ))
                .resources(List.of(
                    "arn:aws:ssm:*:"+getAccount()+":parameter/*"
                ))
                .build()
            )),
          network.getOutputParameters());
    
        CfnOutput httpsListenerOutput = new CfnOutput(this, "loadbalancerDnsName", CfnOutputProps.builder()
          .exportName("loadbalancerDnsName")
          .value(network.getLoadBalancer().getLoadBalancerDnsName())
          .build());
      }
    
}
