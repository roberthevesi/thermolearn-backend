package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.models.Log;
import thermolearn.backend.api.models.ThermostatStatusLogs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ThermostatStatusLogsRepository extends JpaRepository<ThermostatStatusLogs, Long> {
    List<ThermostatStatusLogs> findAllByThermostatIdAndUserId(UUID thermostatId, Long userId);

    List<ThermostatStatusLogs> findAllByThermostatIdAndUserIdAndTimestampAfterAndTimestampBefore(UUID thermostatId, Long userId, LocalDateTime timestampAfter, LocalDateTime timestampBefore);}
