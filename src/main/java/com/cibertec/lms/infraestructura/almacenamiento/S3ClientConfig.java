package com.cibertec.lms.infraestructura.almacenamiento;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/** Conecta al Storage de Supabase por su protocolo compatible con S3. */
@Configuration
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(
            @Value("${lms.almacenamiento.endpoint}") String endpoint,
            @Value("${lms.almacenamiento.region}") String region,
            @Value("${lms.almacenamiento.access-key-id}") String accessKeyId,
            @Value("${lms.almacenamiento.secret-access-key}") String secretAccessKey) {

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(
            @Value("${lms.almacenamiento.endpoint}") String endpoint,
            @Value("${lms.almacenamiento.region}") String region,
            @Value("${lms.almacenamiento.access-key-id}") String accessKeyId,
            @Value("${lms.almacenamiento.secret-access-key}") String secretAccessKey) {

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
