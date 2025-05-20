package kr.loghub.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class R2Config {
    @Bean
    fun r2Client(
        @Value("\${r2.access-key}") accessKey: String,
        @Value("\${r2.secret-key}") secretKey: String,
        @Value("\${r2.endpoint}") endpoint: String,
    ): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey);
        val serviceConfiguration = S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build();

        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of("auto"))
            .serviceConfiguration(serviceConfiguration)
            .build();
    }
}