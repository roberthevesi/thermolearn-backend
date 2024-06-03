package thermolearn.backend.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import thermolearn.backend.api.entities.DayOfTheWeek;

import java.sql.Time;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private UUID thermostatId;
    @Enumerated(EnumType.STRING)
    private DayOfTheWeek day;
    private LocalTime startTime;
    private Float desiredTemperature;
}
