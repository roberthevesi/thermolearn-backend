package thermolearn.backend.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.services.SecretsManagerService;

import java.util.List;


@Service
public class AwsIotService {

    private final IotClient iotClient;

    @Autowired
    public AwsIotService(SecretsManagerService secretsManagerService) {
        String accessKeyId = secretsManagerService.getSecretValue("AWS_ACCESS_KEY_ID");
        String secretAccessKey = secretsManagerService.getSecretValue("AWS_SECRET_ACCESS_KEY");
        String region = secretsManagerService.getSecretValue("AWS_REGION");

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.iotClient = IotClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(region))
                .build();
    }

    public String createThing(String thingName) {
        try {
            CreateThingRequest request = CreateThingRequest.builder()
                    .thingName(thingName)
                    .build();
            CreateThingResponse response = iotClient.createThing(request);
            return response.thingArn();
        } catch (IotException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create thing: " + e.getMessage());
        }
    }

    public void deleteThing(String thingName) {
        try {
            detachThingPrincipals(thingName);
            DeleteThingRequest request = DeleteThingRequest.builder()
                    .thingName(thingName)
                    .build();
            iotClient.deleteThing(request);
        } catch (IotException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete thing: " + e.getMessage());
        }
    }

    public void detachThingPrincipals(String thingName) {
        try {
            ListThingPrincipalsRequest listRequest = ListThingPrincipalsRequest.builder()
                    .thingName(thingName)
                    .build();
            ListThingPrincipalsResponse listResponse = iotClient.listThingPrincipals(listRequest);
            List<String> principals = listResponse.principals();

            for (String principal : principals) {
                DetachThingPrincipalRequest detachRequest = DetachThingPrincipalRequest.builder()
                        .thingName(thingName)
                        .principal(principal)
                        .build();
                iotClient.detachThingPrincipal(detachRequest);
            }
        } catch (IotException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to detach principals from thing: " + e.getMessage());
        }
    }

    public CreateKeysAndCertificateResponse createKeysAndCertificate() {
        CreateKeysAndCertificateRequest request = CreateKeysAndCertificateRequest.builder()
                .setAsActive(true)
                .build();
        return iotClient.createKeysAndCertificate(request);
    }

    public void attachPolicy(String policyName, String certificateArn) {
        AttachPolicyRequest request = AttachPolicyRequest.builder()
                .policyName(policyName)
                .target(certificateArn)
                .build();
        iotClient.attachPolicy(request);
    }

    public void attachThingPrincipal(String thingName, String certificateArn) {
        AttachThingPrincipalRequest request = AttachThingPrincipalRequest.builder()
                .thingName(thingName)
                .principal(certificateArn)
                .build();
        iotClient.attachThingPrincipal(request);
    }
}