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
    private String AWS_ACCESS_KEY_ID;
    private String AWS_SECRET_ACCESS_KEY;
    private String AWS_REGION;
    private String EMAIL_SENDER;
    private AmazonSimpleEmailService sesClient;

    @Autowired
    private SecretsManagerService secretsManagerService;

    @PostConstruct
    public void init() {
        this.AWS_ACCESS_KEY_ID = secretsManagerService.getSecretValue("AWS_ACCESS_KEY_ID");
        this.AWS_SECRET_ACCESS_KEY = secretsManagerService.getSecretValue("AWS_SECRET_ACCESS_KEY");
        this.AWS_REGION = secretsManagerService.getSecretValue("AWS_REGION");
        this.EMAIL_SENDER = secretsManagerService.getSecretValue("EMAIL_SENDER");

        System.out.println("EMAIL_SENDER: " + EMAIL_SENDER);
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
