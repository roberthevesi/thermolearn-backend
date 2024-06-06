package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thermolearn.backend.api.models.ThermostatStatusLogs;
import thermolearn.backend.api.services.ThermostatStatusLogsService;

@RestController
@RequestMapping("/api/v1/thermostat-log")
@RequiredArgsConstructor
public class ThermostatStatusLogsController {
    private final ThermostatStatusLogsService thermostatStatusLogsService;

    @PostMapping("/save-log")
    public ResponseEntity<?> saveLog(
            @RequestBody ThermostatStatusLogs log
    ) {
        try {
            return ResponseEntity.ok(thermostatStatusLogsService.saveLog(log));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
