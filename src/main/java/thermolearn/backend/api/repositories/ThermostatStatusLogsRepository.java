package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.models.Log;
import thermolearn.backend.api.models.ThermostatStatusLogs;

@Repository
public interface ThermostatStatusLogsRepository extends JpaRepository<ThermostatStatusLogs, Long> {
}
