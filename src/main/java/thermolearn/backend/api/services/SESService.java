package thermolearn.backend.api.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SESService {
    @Value("${AWS_ACCESS_KEY_ID}")
    private String AWS_ACCESS_KEY_ID;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String AWS_SECRET_ACCESS_KEY;
    @Value("${AWS_REGION}")
    private String AWS_REGION;
    @Value("${EMAIL_SENDER}")
    private String EMAIL_SENDER;
    private AmazonSimpleEmailService sesClient;
    @PostConstruct
    public void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY_ID,
                AWS_SECRET_ACCESS_KEY
        );

        this.sesClient = AmazonSimpleEmailServiceClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(AWS_REGION)
                .build();
    }

    public void sendEmail(String to, String subject, String body) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(to)
                )
                .withMessage(
                        new Message()
                                .withBody(
                                        new Body().withHtml(new Content().withCharset("UTF-8").withData(body))
                                )
                                .withSubject(
                                        new Content().withCharset("UTF-8").withData(subject)
                                )
                )
                .withSource(EMAIL_SENDER);

        sesClient.sendEmail(request);
    }
}
