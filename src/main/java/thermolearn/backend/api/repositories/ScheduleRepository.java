package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.entities.DayOfTheWeek;
import thermolearn.backend.api.models.Schedule;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
    List<Schedule> findByThermostatIdAndUserId(UUID thermostatId, Long userId);
    List<Schedule> findByThermostatId(UUID thermostatId);

    Schedule findByThermostatIdAndUserIdAndDayAndStartTimeAndDesiredTemperature(UUID thermostatId, Long userId, DayOfTheWeek day, LocalTime time, Float desiredTemperature);
}
