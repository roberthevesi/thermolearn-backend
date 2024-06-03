package thermolearn.backend.api.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import thermolearn.backend.api.services.SecretsManagerService;

@Configuration
public class AwsIotConfig {
        @Value("${AWS_ACCESS_KEY_ID}")
    private String AWS_ACCESS_KEY_ID;
        @Value("${AWS_SECRET_ACCESS_KEY}")
    private String AWS_SECRET_ACCESS_KEY;
        @Value("${AWS_REGION}")
    private String REGION;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)))
                .build();
    }

    @Bean
    public AWSIotDataClient awsIotDataClient() {
        AWSIotDataClientBuilder builder = AWSIotDataClientBuilder.standard();
        builder.setRegion(REGION);
        return (AWSIotDataClient) builder.build();
    }

    @Bean
    public AmazonS3 amazonS3Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY_ID,
                AWS_SECRET_ACCESS_KEY
        );

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(REGION)
                .build();
    }
}