package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
