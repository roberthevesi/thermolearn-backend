package thermolearn.backend.api.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDistanceFromHomeRequest {
    private Long userId;
    private Integer distanceFromHome;
}