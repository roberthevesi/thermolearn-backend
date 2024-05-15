package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thermolearn.backend.api.utils.DeviceShadowService;
import thermolearn.backend.api.utils.MqttPublisher;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {
    @Autowired
    private MqttPublisher mqttPublisher;
    @Autowired
    private DeviceShadowService deviceShadowService;
    @PostMapping
    public ResponseEntity<Object> demo(@RequestParam String mode, @RequestParam Double desiredTemp) {
        deviceShadowService.updateShadow(mode, desiredTemp);
        return ResponseEntity.ok().build();
//        try{
//            mqttPublisher.publish("pi/commands", "Hello, World, from backend!");
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//        return "Hello, World!";

    }
}