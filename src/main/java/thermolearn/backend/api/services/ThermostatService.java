package thermolearn.backend.api.services;


import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.Thermostat;
import thermolearn.backend.api.models.User;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.ThermostatRepository;
import thermolearn.backend.api.repositories.UserRepository;
import thermolearn.backend.api.utils.EncryptionUtils;

import java.io.IOException;
import java.util.UUID;

@Service
@NoArgsConstructor(force = true)
public class ThermostatService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ThermostatRepository thermostatRepository;
    @Autowired
    private final PairedThermostatRepository pairedThermostatRepository;
    @Autowired
    private final QRCodeService qrCodeService;
    @Autowired
    private final S3Service s3Service;
    @Autowired
    private final EncryptionUtils encryptionUtils;

    @Transactional
    public Thermostat addThermostat(String model) throws Exception {
        Thermostat thermostat = Thermostat
                .builder()
                .model(model)
                .isPaired(false)
                .build();

        assert thermostatRepository != null;
        String thermostatId = thermostatRepository.save(thermostat).getId().toString();

        assert encryptionUtils != null;
        String encryptedThermostatId = encryptionUtils.encrypt(thermostatId);

        assert qrCodeService != null;
        byte[] qrCodeImage = qrCodeService.generateQRCode(encryptedThermostatId, 250, 250);

        String folder = "thermostats_qr_codes/";
        String key = folder + encryptedThermostatId + ".jpg";

        assert s3Service != null;
        String qrCodeURL = s3Service.uploadFile("thermolearn-bucket", qrCodeImage, key, "image/jpeg");

        thermostat.setQrCodeURL(qrCodeURL);
        thermostat.setEncryptedId(encryptedThermostatId);
        thermostatRepository.save(thermostat);

        return thermostat;
    }

    @Transactional
    public PairedThermostat pairThermostat(String encryptedThermostatId, Long userId) throws Exception {
        assert encryptionUtils != null;
        String decryptedThermostatId = encryptionUtils.decrypt(encryptedThermostatId);

        assert userRepository != null;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        assert thermostatRepository != null;
        Thermostat thermostat = thermostatRepository.findById(UUID.fromString(decryptedThermostatId)).orElseThrow(
                () -> new RuntimeException("Thermostat not found")
        );

        if(thermostat.getIsPaired())
            throw new RuntimeException("Thermostat is already paired");

        thermostat.setIsPaired(true);

        PairedThermostat pairedThermostat = PairedThermostat
                .builder()
                .thermostatId(UUID.fromString(decryptedThermostatId))
                .userId(userId)
                .build();

        assert pairedThermostatRepository != null;
        return pairedThermostatRepository.save(pairedThermostat);
    }

    @Transactional
    public boolean unPairThermostat(String encryptedThermostatId, Long userId) throws Exception {
        assert encryptionUtils != null;
        String decryptedThermostatId = encryptionUtils.decrypt(encryptedThermostatId);

        assert userRepository != null;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        assert thermostatRepository != null;
        Thermostat thermostat = thermostatRepository.findById(UUID.fromString(decryptedThermostatId)).orElseThrow(
                () -> new RuntimeException("Thermostat not found")
        );

        assert pairedThermostatRepository != null;
        PairedThermostat pairedThermostat = pairedThermostatRepository.findByThermostatIdAndUserId(UUID.fromString(decryptedThermostatId), userId).orElseThrow(
                () -> new RuntimeException("Pairing not found")
        );

        thermostat.setIsPaired(false);
        thermostatRepository.save(thermostat);

        pairedThermostatRepository.delete(pairedThermostat);

        return true;
    }

}
