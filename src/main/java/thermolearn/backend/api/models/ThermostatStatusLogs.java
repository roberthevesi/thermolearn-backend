package thermolearn.backend.api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import thermolearn.backend.api.entities.LogEventType;
import thermolearn.backend.api.entities.ThermostatStatusLogType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thermostat_status_logs")
public class ThermostatStatusLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID thermostatId;
    @Enumerated(EnumType.STRING)
    private ThermostatStatusLogType status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
}
