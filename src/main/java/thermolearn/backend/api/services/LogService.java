package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.Log;
import thermolearn.backend.api.models.User;
import thermolearn.backend.api.repositories.LogRepository;
import thermolearn.backend.api.repositories.UserRepository;

@Service
@NoArgsConstructor(force = true)
public class LogService {
    @Autowired
    private final LogRepository logRepository;

    @Autowired
    private final UserRepository userRepository;

    public Log saveLog(Log log) {
        assert userRepository != null;
        User user = userRepository.findById(log.getUserId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        assert logRepository != null;
        return logRepository.save(log);
    }
}
