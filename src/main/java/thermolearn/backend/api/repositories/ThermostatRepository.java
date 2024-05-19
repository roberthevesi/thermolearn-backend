package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.models.Thermostat;

import java.util.UUID;

@Repository
public interface ThermostatRepository extends JpaRepository<Thermostat, UUID> {
}
