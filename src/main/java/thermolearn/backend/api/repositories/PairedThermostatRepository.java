package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.entities.AuthenticationResponse;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.ThermostatStatusLogs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PairedThermostatRepository extends JpaRepository<PairedThermostat, Long> {
    Optional<PairedThermostat> findByThermostatIdAndUserId(UUID thermostatId, Long userId);
    List<PairedThermostat> findPairedThermostatsByUserId(Long userId);
    PairedThermostat findByThermostatId(UUID thermostatId);
}
