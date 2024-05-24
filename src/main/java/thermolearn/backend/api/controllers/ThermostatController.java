package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import thermolearn.backend.api.entities.CreateThermostatRequest;
import thermolearn.backend.api.entities.PairThermostatRequest;
import thermolearn.backend.api.entities.ScheduleRequest;
import thermolearn.backend.api.services.ScheduleService;
import thermolearn.backend.api.services.ThermostatService;
import thermolearn.backend.api.utils.AwsIotService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/thermostat")
@RequiredArgsConstructor
public class ThermostatController {
    @Autowired
    private final ThermostatService thermostatService;
    @Autowired
    private final ScheduleService scheduleService;

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

//    @Autowired
//    private AwsIotService awsIotService;
//
//    @PostMapping("/create-thing")
//    public String pairNewThermostat(@RequestParam String thermostatId) {
//        String thingName = "thermostat_" + thermostatId;
//        String thingArn = awsIotService.createThing(thingName);
//
//        CreateKeysAndCertificateResponse keysAndCert = awsIotService.createKeysAndCertificate();
//        String certificateArn = keysAndCert.certificateArn();
//
//        awsIotService.attachPolicy("RaspberryPiPolicy", certificateArn);
//        awsIotService.attachThingPrincipal(thingName, certificateArn);
//
//        // Store keysAndCert.certPem(), keysAndCert.keyPair().privateKey(), etc. securely
//
//        return thingArn;
//    }

//    @DeleteMapping("/delete-thing")
//    public String deleteThermostat(@RequestParam String thermostatId) {
//        String thingName = "thermostat_" + thermostatId;
//        awsIotService.deleteThing(thingName);
//        return "Thing " + thingName + " deleted successfully.";
//    }


}
