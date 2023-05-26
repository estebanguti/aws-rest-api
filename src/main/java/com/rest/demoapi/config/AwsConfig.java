package com.rest.demoapi.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("demo")
public class AwsConfig {

    @Value("${aws.key}")
    private String accessKey;

    @Value("${aws.secret}")
    private String secretKey;

    @Bean
    public AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(credentials())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Bean
    public AmazonSNS snsClient(){
        return AmazonSNSClientBuilder.standard()
                .withCredentials(credentials())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Bean
    public AmazonSQS sqsClient(){
        return AmazonSQSClientBuilder.standard()
                .withCredentials(credentials())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Bean
    public AWSLambda lambdaClient(){
        return AWSLambdaClientBuilder.standard()
                .withCredentials(credentials())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private AWSStaticCredentialsProvider credentials() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(basicAWSCredentials);
    }

}
