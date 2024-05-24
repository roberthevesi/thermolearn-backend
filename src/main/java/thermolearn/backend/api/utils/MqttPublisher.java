package thermolearn.backend.api.utils;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import thermolearn.backend.api.services.SecretsManagerService;
import thermolearn.backend.api.utils.SslUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;


@Component
public class MqttPublisher {

    private String clientEndpoint;
    private String clientId;
    private String certificateFile;
    private String privateKeyFile;
    private String keystorePassword;

    private MqttClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SecretsManagerService secretsManagerService;

    @PostConstruct
    public void init() throws Exception {
        this.clientEndpoint = secretsManagerService.getSecretValue("AWS_IOT_CLIENT_ENDPOINT");
        this.clientId = secretsManagerService.getSecretValue("AWS_IOT_CLIENT_ID");
        this.certificateFile = secretsManagerService.getSecretValue("AWS_CERTIFICATE_FILE");
        this.privateKeyFile = secretsManagerService.getSecretValue("AWS_PRIVATE_KEY_FILE");
        this.keystorePassword = secretsManagerService.getSecretValue("AWS_KEYSTORE_PASSWORD");

        String brokerUrl = "ssl://" + clientEndpoint + ":8883";
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(brokerUrl, clientId, persistence);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setSocketFactory(SslUtil.getSocketFactory(certificateFile, privateKeyFile, keystorePassword));
        client.connect(connOpts);
    }

    public void publish(String topic, String key, String value, boolean retained) throws MqttException {
        String jsonPayload = convertToJson(key, value);
        MqttMessage message = new MqttMessage(jsonPayload.getBytes());
        message.setQos(0);
        message.setRetained(retained);
        client.publish(topic, message);
    }

    public void publishMode(String thermostatId, String mode) throws MqttException {
        String topic = "thermostat/" + thermostatId + "/mode";
        publish(topic, "mode", mode, true);  // Publish mode with retained flag
    }

    private String convertToJson(String key, String value) {
        try {
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put(key, value);
            return objectMapper.writeValueAsString(payloadMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }
}

