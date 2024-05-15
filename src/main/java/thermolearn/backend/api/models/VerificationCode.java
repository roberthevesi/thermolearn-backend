package thermolearn.backend.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import thermolearn.backend.api.entities.VerificationCodeType;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VerificationCodes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String code;
    private LocalDateTime generationTime;
    private LocalDateTime expirationTime;
    private boolean used;
    @Enumerated(EnumType.STRING)
    private VerificationCodeType type;

    public static String generateCode(){
        String CHARACTERS = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        int LENGTH = 6;

        Random random = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
