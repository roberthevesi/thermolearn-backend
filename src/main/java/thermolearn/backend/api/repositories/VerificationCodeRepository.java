package thermolearn.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import thermolearn.backend.api.entities.VerificationCodeType;
import thermolearn.backend.api.models.VerificationCode;

import java.time.LocalDateTime;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    @Query("SELECT v FROM VerificationCode v WHERE v.code = :code AND v.userEmail = :userEmail AND v.expirationTime > :now AND v.type = :type")
    VerificationCode findValidCode(
            @Param("code") String code,
            @Param("userEmail") String userEmail,
            @Param("now") LocalDateTime now,
            @Param("type") VerificationCodeType type);
}
