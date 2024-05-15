package thermolearn.backend.api.configs;

import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsIotConfig {

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AWSIotDataClient awsIotDataClient() {
        AWSIotDataClientBuilder builder = AWSIotDataClientBuilder.standard();
        builder.setRegion(region);
        return (AWSIotDataClient) builder.build();
    }
}
