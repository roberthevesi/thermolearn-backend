package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.ThermostatStatusLogs;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.ThermostatStatusLogsRepository;

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
        System.out.println(log.getThermostatId());
        PairedThermostat pairedThermostat = pairedThermostatRepository.findByThermostatId(log.getThermostatId());
        if (pairedThermostat == null) {
            throw new IllegalArgumentException("Thermostat not found");
        }

        assert thermostatStatusLogsRepository != null;
        return thermostatStatusLogsRepository.save(log);
    }
}
