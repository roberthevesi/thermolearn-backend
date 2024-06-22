package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import thermolearn.backend.api.entities.CreateThermostatRequest;
import thermolearn.backend.api.entities.PairThermostatRequest;
import thermolearn.backend.api.entities.ScheduleRequest;
import thermolearn.backend.api.entities.UpdateTemperatureRequest;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.services.ScheduleService;
import thermolearn.backend.api.services.ThermostatService;
import thermolearn.backend.api.utils.AwsIotService;
import thermolearn.backend.api.utils.MqttPublisher;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/thermostat")
@RequiredArgsConstructor
public class ThermostatController {
    @Autowired
    private final ThermostatService thermostatService;
    @Autowired
    private final ScheduleService scheduleService;
    @Autowired
    private MqttPublisher mqttPublisher;

    @PostMapping("/create-thermostat")
    public ResponseEntity<?> createThermostat(
            @RequestBody CreateThermostatRequest request
    ) {
        try {
            return ResponseEntity.ok(thermostatService.createThermostat(request.getModel(), request.getMacAddress()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-thermostat-by-mac-address")
    public ResponseEntity<?> getThermostatByMacAddress(
            @RequestParam String macAddress
    ) {
        try {
            return ResponseEntity.ok(thermostatService.getThermostatByMacAddress(macAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/is-thermostat-ready-to-pair")
    public ResponseEntity<?> isThermostatReadyToPair(
            @RequestParam String thermostatId,
            @RequestParam Long userId
            ) {
        try {
            return ResponseEntity.ok(thermostatService.isThermostatReadyToPair(thermostatId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/set-thermostat-fingerprint")
    public ResponseEntity<?> setThermostatFingerprint(
            @RequestParam String thermostatId,
            @RequestParam String fingerprint
    ) {
        try {
            return ResponseEntity.ok(thermostatService.setThermostatFingerprint(thermostatId, fingerprint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-thermostat-fingerprint")
    public ResponseEntity<?> getThermostatFingerprint(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(thermostatService.getThermostatFingerprint(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pair-thermostat")
    public ResponseEntity<?> pairThermostat(
            @RequestBody PairThermostatRequest request
    ) {
        try {
            return ResponseEntity.ok(thermostatService.pairThermostat(request.getThermostatId(), request.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unpair-thermostat")
    public ResponseEntity<?> unPairThermostat(
            @RequestBody PairThermostatRequest request
    ) {
        try {
            mqttPublisher.init();
            mqttPublisher.publishUnpairRequest(request.getThermostatId());
            return ResponseEntity.ok(thermostatService.unPairThermostat(request.getThermostatId(), request.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-schedule")
    public ResponseEntity<?> getSchedule(
            @RequestParam String thermostatId,
            @RequestParam Long userId
            ) {
        try {
            return ResponseEntity.ok(scheduleService.getSchedule(UUID.fromString(thermostatId), userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-schedule-by-thermostat-id")
    public ResponseEntity<?> getScheduleByThermostatId(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(scheduleService.getScheduleByThermostatId(UUID.fromString(thermostatId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-schedule")
    public ResponseEntity<?> addSchedule(
            @RequestBody ScheduleRequest request
    ) {
        try {
            return ResponseEntity.ok(scheduleService.addSchedule(request.getDay(), request.getTime(), UUID.fromString(request.getThermostatId()), request.getUserId(), request.getDesiredTemperature()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-schedule")
    public ResponseEntity<?> deleteSchedule(
            @RequestBody ScheduleRequest request
    ) {
        try {
            return ResponseEntity.ok(scheduleService.deleteSchedule(request.getDay(), request.getTime(), UUID.fromString(request.getThermostatId()), request.getUserId(), request.getDesiredTemperature()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-temperature")
    public boolean updateTemperature(@RequestBody UpdateTemperatureRequest request) throws Exception {
        mqttPublisher.init();
        return mqttPublisher.publishTemperatureRequest(request.getThermostatId(), request.getTemperature());
    }

    @PostMapping("/update-schedule-request")
    public boolean updateScheduleRequest(@RequestParam String thermostatId) throws Exception {
        mqttPublisher.init();
        return mqttPublisher.publishUpdatedScheduleRequest(thermostatId);
    }

    @GetMapping("/get-target-temperature")
    public ResponseEntity<?> getTargetTemperature(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(mqttPublisher.getTargetTemperature(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-thermostat-status")
    public ResponseEntity<?> getThermostatStatus(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(mqttPublisher.getThermostatStatus(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
