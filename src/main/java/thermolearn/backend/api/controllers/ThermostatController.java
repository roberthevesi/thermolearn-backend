package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thermolearn.backend.api.entities.PairThermostatRequest;
import thermolearn.backend.api.services.ThermostatService;

@RestController
@RequestMapping("/api/v1/thermostat")
@RequiredArgsConstructor
public class ThermostatController {
    @Autowired
    private final ThermostatService thermostatService;

    @PostMapping("/add-thermostat")
    public ResponseEntity<?> addThermostat(
            @RequestParam String model
    ) {
        try {
            return ResponseEntity.ok(thermostatService.addThermostat(model));
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
}
