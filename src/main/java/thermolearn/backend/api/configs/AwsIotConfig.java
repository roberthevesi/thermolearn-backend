package thermolearn.backend.api.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsIotConfig {
    @Value("${AWS_ACCESS_KEY_ID}")
    private String AWS_ACCESS_KEY_ID;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String AWS_SECRET_ACCESS_KEY;
    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AWSIotDataClient awsIotDataClient() {
        AWSIotDataClientBuilder builder = AWSIotDataClientBuilder.standard();
        builder.setRegion(region);
        return (AWSIotDataClient) builder.build();
    }

    @Bean
    public AmazonS3 amazonS3Client(){
//        Dotenv dotenv = Dotenv.load();

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY_ID,
                AWS_SECRET_ACCESS_KEY
        );

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }
}
