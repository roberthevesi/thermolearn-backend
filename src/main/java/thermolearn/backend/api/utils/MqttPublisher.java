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
import thermolearn.backend.api.models.Thermostat;
import thermolearn.backend.api.repositories.ThermostatRepository;
import thermolearn.backend.api.services.SecretsManagerService;
import thermolearn.backend.api.utils.SslUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MqttPublisher {

    private String clientEndpoint;
    private String clientId;
    private String certificateFile;
    private String privateKeyFile;
    private String keystorePassword;

    @Autowired
    private ThermostatRepository thermostatRepository;

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

        InputStream certInputStream = getClass().getClassLoader().getResourceAsStream(certificateFile);
        InputStream keyInputStream = getClass().getClassLoader().getResourceAsStream(privateKeyFile);

        if (certInputStream == null) {
            throw new RuntimeException("Certificate file not found in classpath: " + certificateFile);
        }

        if (keyInputStream == null) {
            throw new RuntimeException("Private key file not found in classpath: " + privateKeyFile);
        }

        String broker = "ssl://" + clientEndpoint + ":8883";
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(broker, clientId, persistence);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setSocketFactory(SslUtil.getSocketFactory(certInputStream, keyInputStream, keystorePassword));

        client.connect(connOpts);
    }

//    public void publish(String topic, String key, String value, boolean retained) throws MqttException {
//        String jsonPayload = convertToJson(key, value);
//        MqttMessage message = new MqttMessage(jsonPayload.getBytes());
//        message.setQos(0);
//        message.setRetained(retained);
//        client.publish(topic, message);
//    }

    public void publishMultiple(String topic, Map<String, String> keyValuePairs, boolean retained) throws MqttException {
        String jsonPayload = convertToJson(keyValuePairs);
        MqttMessage message = new MqttMessage(jsonPayload.getBytes());
        message.setQos(0);
        message.setRetained(retained);
        client.publish(topic, message);
    }

//    public void publishMode(String thermostatId, String mode) throws MqttException {
//        String topic = "thermostat/" + thermostatId + "/mode";
//        publish(topic, "mode", mode, true);
//    }

    public boolean publishTemperatureRequest(String thermostatId, Float temperature) {
        try{
            Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(() -> new RuntimeException("Thermostat not found"));
            if (!thermostat.getIsPaired()) {
                throw new RuntimeException("Thermostat is not paired");
            }

            String topic = "thermostats/" + thermostatId + "/temperatureRequests";
            Map<String, String> keyValuePairs = new HashMap<>();
            keyValuePairs.put("desiredTemp", temperature.toString());

            long epochMillis = System.currentTimeMillis();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            keyValuePairs.put("timestamp", formattedDateTime);

            publishMultiple(topic, keyValuePairs, true);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish temperature request", e);
        }
    }

    public boolean publishUpdatedScheduleRequest(String thermostatId){
        try{
            Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(() -> new RuntimeException("Thermostat not found"));
            if (!thermostat.getIsPaired()) {
                throw new RuntimeException("Thermostat is not paired");
            }

            String topic = "thermostats/" + thermostatId + "/updatedScheduleRequests";
            Map<String, String> keyValuePairs = new HashMap<>();
            keyValuePairs.put("updateSchedule", "true");

            long epochMillis = System.currentTimeMillis();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            keyValuePairs.put("timestamp", formattedDateTime);

            publishMultiple(topic, keyValuePairs, true);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish updated schedule request", e);
        }
    }

    public boolean publishUnpairRequest(String thermostatId) throws MqttException {
        try{
            Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(() -> new RuntimeException("Thermostat not found"));
            if (!thermostat.getIsPaired()) {
                throw new RuntimeException("Thermostat is not paired");
            }

            String topic = "thermostats/" + thermostatId + "/unpairRequests";
            Map<String, String> keyValuePairs = new HashMap<>();
            keyValuePairs.put("unpair", "true");

            long epochMillis = System.currentTimeMillis();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            keyValuePairs.put("timestamp", formattedDateTime);

            publishMultiple(topic, keyValuePairs, true);
            return true;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to publish unpair request", e);
        }
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

    private String convertToJson(Map<String, String> keyValuePairs) {
        try {
            return objectMapper.writeValueAsString(keyValuePairs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }

    public String fetchValueFromTopic(String topic, String key) {
        try {
            final String[] result = {null};
            client.subscribe(topic, (topic1, message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                try {
                    Map<String, Object> map = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
                    if (map.containsKey(key)) {
                        result[0] = map.get(key).toString();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse JSON payload", e);
                }
                synchronized (result) {
                    result.notify();
                }
            });

            synchronized (result) {
                result.wait(5000); // wait for 5 seconds for a message
            }

            return result[0];
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch value from topic", e);
        } finally {
            try {
                client.unsubscribe(topic);
            } catch (MqttException e) {
                throw new RuntimeException("Failed to unsubscribe from topic", e);
            }
        }
    }

    public String getTargetTemperature(String thermostatId) {
        try {
            Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(() -> new RuntimeException("Thermostat not found"));
            if (!thermostat.getIsPaired()) {
                throw new RuntimeException("Thermostat is not paired");
            }

            String topic = "thermostats/" + thermostatId + "/temperatureRequests";
            return fetchValueFromTopic(topic, "desiredTemp");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get target temperature", e);
        }
    }

    public Map<String, String> getThermostatStatus(String thermostatId) {
        try {
            Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(() -> new RuntimeException("Thermostat not found"));
            if (!thermostat.getIsPaired()) {
                throw new RuntimeException("Thermostat is not paired");
            }

            String topic = "thermostats/" + thermostatId + "/status";
            Map<String, String> statusMap = new HashMap<>();

            List<String> keys = List.of("ambientTemperature", "heatingStatus", "ambientHumidity");

            for (String key : keys) {
                String value = fetchValueFromTopic(topic, key);
                if (value != null) {
                    statusMap.put(key, value);
                }
            }

            return statusMap;
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            throw new RuntimeException("Failed to get thermostat status", e);
        }
    }


    public void disconnect() throws MqttException {
        client.disconnect();
    }
}
