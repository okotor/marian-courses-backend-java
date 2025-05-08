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
        if (accessKey == null) {
            accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
        }

        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (secretKey == null) {
            secretKey = System.getProperty("AWS_SECRET_ACCESS_KEY");
        }

//        if (accessKey != null && secretKey != null) {
//            System.out.println("AWS_ACCESS_KEY_ID is set: " + (accessKey != null));
//            System.out.println("AWS_SECRET_ACCESS_KEY is set: " + (secretKey != null));
//        } else {
//            System.out.println("AWS credentials are missing.");
//        }

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