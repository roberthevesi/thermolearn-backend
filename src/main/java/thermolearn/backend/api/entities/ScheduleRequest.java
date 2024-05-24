package thermolearn.backend.api.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
    private DayOfTheWeek day;
    private LocalTime time;
    private String thermostatId;
    private Long userId;
    private Float desiredTemperature;
}
