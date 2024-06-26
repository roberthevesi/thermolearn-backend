package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thermolearn.backend.api.models.Log;
import thermolearn.backend.api.services.LogService;

@RestController
@RequestMapping("/api/v1/log")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @PostMapping("/save-log")
    public ResponseEntity<?> saveLog(
            @RequestBody Log log
    ) {
        try {
            return ResponseEntity.ok(logService.saveLog(log));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-user-logs-by-thermostat-id")
    public ResponseEntity<?> getUserLogsByThermostatId(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(logService.getLogsByThermostatId(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-latest-user-log-by-thermostat-id")
    public ResponseEntity<?> getLatestUserLogByThermostatId(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(logService.getLatestLogByThermostatId(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
