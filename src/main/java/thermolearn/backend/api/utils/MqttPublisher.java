package thermolearn.backend.api.utils;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Map;

@Component
public class MqttPublisher {
    @Value("${AWS_IOT_CLIENT_ENDPOINT}")
    private String CLIENT_ENDPOINT;
    @Value("${AWS_KEYSTORE_PASSWORD}")
    private String AWS_KEYSTORE_PASSWORD;
    private AWSIotMqttClient client;

    @PostConstruct
    public void init() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        ClassPathResource resource = new ClassPathResource("certs/keystore.jks");
        try(InputStream keyStoreInputStream = resource.getInputStream()) {
            keyStore.load(keyStoreInputStream, AWS_KEYSTORE_PASSWORD.toCharArray());
        }
        String clientId = "ThermolearnBackendClientId";
        client = new AWSIotMqttClient(CLIENT_ENDPOINT, clientId, keyStore, AWS_KEYSTORE_PASSWORD);
        client.connect();
    }

    public void publish(String topic, String payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(Map.of("message", payload));
        byte[] bytesPayload = jsonPayload.getBytes(StandardCharsets.UTF_8);
        AWSIotMessage message = new AWSIotMessage(topic, AWSIotQos.QOS0, bytesPayload);
        client.publish(message);
    }
}