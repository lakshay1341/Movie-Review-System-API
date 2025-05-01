/*
package in.lakshay.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import in.lakshay.service.S3BucketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class S3BucketServiceConfig {

    @Bean
    @Profile("!h2")
    public S3BucketService productionS3BucketService(
            @Value("${aws.access.key.id}") String accessKey,
            @Value("${aws.secret.access.key}") String secretKey,
            @Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.s3.region:us-east-1}") String region) {
        return new S3BucketService(accessKey, secretKey, bucketName, region);
    }

    @Bean
    @Profile("h2")
    @Primary
    public S3BucketService mockS3BucketService() {
        // Create a mock S3 client that doesn't actually connect to AWS
        AWSCredentials credentials = new BasicAWSCredentials("dummy-access-key", "dummy-secret-key");
        AmazonS3 mockS3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        // Return a new S3BucketService with dummy credentials
        return new S3BucketService("dummy-access-key", "dummy-secret-key", "dummy-bucket-name", "us-east-1");
    }
}
*/
