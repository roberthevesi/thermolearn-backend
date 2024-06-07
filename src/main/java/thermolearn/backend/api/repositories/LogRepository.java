package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.models.Log;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByUserId(Long userId);
    Log findFirstByUserIdOrderByTimestampDesc(Long userId);
}
