package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.ThermostatStatusLogs;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.ThermostatStatusLogsRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor(force = true)
public class ThermostatStatusLogsService {
    @Autowired
    private final ThermostatStatusLogsRepository thermostatStatusLogsRepository;

    @Autowired
    private final PairedThermostatRepository pairedThermostatRepository;

    public ThermostatStatusLogs saveLog(ThermostatStatusLogs log) {
        assert pairedThermostatRepository != null;
        PairedThermostat pairedThermostat = pairedThermostatRepository.findByThermostatId(log.getThermostatId());
        if (pairedThermostat == null) {
            throw new IllegalArgumentException("Thermostat is not paired");
        }

        log.setUserId(pairedThermostat.getUserId());

        assert thermostatStatusLogsRepository != null;
        return thermostatStatusLogsRepository.save(log);
    }

    public List<ThermostatStatusLogs> getThermostatStatusLogs(String thermostatId, Long userId) {
        assert pairedThermostatRepository != null;
        Optional<PairedThermostat> pairedThermostat = pairedThermostatRepository.findByThermostatIdAndUserId(UUID.fromString(thermostatId), userId);

        if (pairedThermostat.isEmpty()) {
            throw new IllegalArgumentException("Pairing not found");
        }

        assert thermostatStatusLogsRepository != null;

        LocalDateTime sevenDaysAgo = LocalDateTime.now(ZoneId.systemDefault()).minusDays(7);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        return thermostatStatusLogsRepository.findAllByThermostatIdAndUserIdAndTimestampAfterAndTimestampBefore(UUID.fromString(thermostatId), userId, sevenDaysAgo, now);
    }
}
