package thermolearn.backend.api.utils;

import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class DeviceShadowService {

    @Autowired
    private AWSIotDataClient awsIotDataClient;

    private final String thingName = "RaspberryPiThing";

    public void updateShadow(String mode, Double desiredTemp) {
        String payload = String.format(
                "{\"state\":{\"desired\":{\"mode\":\"%s\", \"desiredTemp\":%.1f}}}",
                mode, desiredTemp
        );
        UpdateThingShadowRequest request = new UpdateThingShadowRequest()
                .withThingName(thingName)
                .withPayload(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8)));
        awsIotDataClient.updateThingShadow(request);
    }

    public String getCurrentMode() {
        GetThingShadowRequest request = new GetThingShadowRequest()
                .withThingName(thingName);
        GetThingShadowResult result = awsIotDataClient.getThingShadow(request);
        byte[] bytes = new byte[result.getPayload().remaining()];
        result.getPayload().get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
