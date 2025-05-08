package com.tehacko.backend_java.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Bean
    public AmazonS3 amazonS3() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1"); // Replace with your region

        if (accessKey != null && secretKey != null) {
            // Use explicit credentials if available
            builder.withCredentials(new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(accessKey, secretKey)
            ));
        } else {
            // Fallback to default credentials provider chain (e.g., IAM roles)
            builder.withCredentials(new DefaultAWSCredentialsProviderChain());
        }

        return builder.build();
    }
}