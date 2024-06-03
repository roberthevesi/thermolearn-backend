package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.models.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
