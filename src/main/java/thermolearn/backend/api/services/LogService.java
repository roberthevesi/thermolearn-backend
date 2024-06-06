package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.Log;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.User;
import thermolearn.backend.api.repositories.LogRepository;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor(force = true)
public class LogService {
    @Autowired
    private final LogRepository logRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PairedThermostatRepository pairedThermostatRepository;

    public Log saveLog(Log log) {
        assert userRepository != null;
        User user = userRepository.findById(log.getUserId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        assert logRepository != null;
        return logRepository.save(log);
    }



    public List<Log> getLogsByThermostatId(String thermostatId) {
        assert pairedThermostatRepository != null;
        PairedThermostat pairedThermostat = pairedThermostatRepository.findByThermostatId(UUID.fromString(thermostatId));

        Long userId = pairedThermostat.getUserId();

        assert logRepository != null;
        return logRepository.findAllByUserId(userId);
    }
}
