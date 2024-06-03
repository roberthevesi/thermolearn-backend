package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.entities.DayOfTheWeek;
import thermolearn.backend.api.models.Schedule;
import thermolearn.backend.api.models.Thermostat;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.ScheduleRepository;
import thermolearn.backend.api.repositories.ThermostatRepository;
import thermolearn.backend.api.repositories.UserRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor(force = true)
public class ScheduleService {
    @Autowired
    private final ScheduleRepository scheduleRepository;
    @Autowired
    private final ThermostatRepository thermostatRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PairedThermostatRepository pairedThermostatRepository;

    public List<Schedule> getSchedule(UUID thermostatId, Long userId) throws Exception {
        assert thermostatRepository != null;
        assert userRepository != null;
        if(thermostatRepository.findById(thermostatId).isEmpty() || userRepository.findById(userId).isEmpty()){
            throw new Exception("Thermostat or user not found");
        }

        assert scheduleRepository != null;
        return scheduleRepository.findByThermostatIdAndUserId(thermostatId, userId);
    }

    public List<Schedule> getScheduleByThermostatId(UUID thermostatId) throws Exception {
        assert thermostatRepository != null;
        if(thermostatRepository.findById(thermostatId).isEmpty()){
            throw new Exception("Thermostat not found");
        }

        assert scheduleRepository != null;
        return scheduleRepository.findByThermostatId(thermostatId);
    }

    public Schedule addSchedule(DayOfTheWeek day, LocalTime time, UUID thermostatId, Long userId, Float desiredTemperature) throws Exception {
        assert thermostatRepository != null;
        assert userRepository != null;
        if(thermostatRepository.findById(thermostatId).isEmpty() || userRepository.findById(userId).isEmpty()){
            throw new Exception("Thermostat or user not found");
        }

        assert pairedThermostatRepository != null;
        if(pairedThermostatRepository.findByThermostatIdAndUserId(thermostatId, userId).isEmpty()){
            throw new Exception("Thermostat not paired with user");
        }

        assert scheduleRepository != null;
        Schedule existingSchedule = scheduleRepository.findByThermostatIdAndUserIdAndDayAndStartTimeAndDesiredTemperature(thermostatId, userId, day, time, desiredTemperature);
        if(existingSchedule != null){
            throw new Exception("Schedule already exists");
        }

        Schedule schedule = Schedule
                .builder()
                .day(day)
                .startTime(time)
                .thermostatId(thermostatId)
                .userId(userId)
                .desiredTemperature(desiredTemperature)
                .build();
        return scheduleRepository.save(schedule);
    }

    public boolean deleteSchedule(DayOfTheWeek day, LocalTime time, UUID thermostatId, Long userId, Float desiredTemperature) throws Exception {
        assert thermostatRepository != null;
        assert userRepository != null;
        if(thermostatRepository.findById(thermostatId).isEmpty() || userRepository.findById(userId).isEmpty()){
            throw new Exception("Thermostat or user not found");
        }

        assert pairedThermostatRepository != null;
        if(pairedThermostatRepository.findByThermostatIdAndUserId(thermostatId, userId).isEmpty()){
            throw new Exception("Thermostat not paired with user");
        }

        assert scheduleRepository != null;
        Schedule existingSchedule = scheduleRepository.findByThermostatIdAndUserIdAndDayAndStartTimeAndDesiredTemperature(thermostatId, userId, day, time, desiredTemperature);
        if(existingSchedule == null){
            throw new Exception("Schedule not found");
        }

        scheduleRepository.delete(existingSchedule);
        return true;
    }
}
