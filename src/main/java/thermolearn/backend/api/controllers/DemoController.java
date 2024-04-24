package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thermolearn.backend.api.utils.MqttPublisher;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    @Autowired
    private MqttPublisher mqttPublisher;
    @GetMapping
    public String demo() {
        try{
            mqttPublisher.publish("pi/commands", "Hello, World, from backend!");
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Hello, World!";

    }
}