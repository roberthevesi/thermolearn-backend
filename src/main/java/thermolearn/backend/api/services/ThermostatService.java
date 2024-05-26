package thermolearn.backend.api.services;


import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import thermolearn.backend.api.models.PairedThermostat;
import thermolearn.backend.api.models.Schedule;
import thermolearn.backend.api.models.Thermostat;
import thermolearn.backend.api.models.User;
import thermolearn.backend.api.repositories.PairedThermostatRepository;
import thermolearn.backend.api.repositories.ScheduleRepository;
import thermolearn.backend.api.repositories.ThermostatRepository;
import thermolearn.backend.api.repositories.UserRepository;
import thermolearn.backend.api.utils.AwsIotService;
import thermolearn.backend.api.utils.EncryptionUtils;

import java.util.List;
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
    private final ScheduleRepository scheduleRepository;
    @Autowired
    private final QRCodeService qrCodeService;
    @Autowired
    private final S3Service s3Service;
    @Autowired
    private final EncryptionUtils encryptionUtils;
    @Autowired
    private AwsIotService awsIotService;

    @Transactional
    public Thermostat createThermostat(String model, String macAddress) throws Exception {
        assert thermostatRepository != null;
        Thermostat existingThermostat = thermostatRepository.findByMacAddress(macAddress);

        if(existingThermostat != null)
            throw new RuntimeException("Thermostat already exists");

        Thermostat thermostat = Thermostat
                .builder()
                .model(model)
                .macAddress(macAddress)
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

    public Thermostat getThermostatByMacAddress(String macAddress) {
        assert thermostatRepository != null;
        Thermostat thermostat = thermostatRepository.findByMacAddress(macAddress);
        if (thermostat == null) {
            throw new RuntimeException("Thermostat not found");
        }
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

        String thingName = createThing(decryptedThermostatId);
        thermostat.setThingName(thingName);
        thermostatRepository.save(thermostat);

        PairedThermostat pairedThermostat = PairedThermostat
                .builder()
                .thermostatId(UUID.fromString(decryptedThermostatId))
                .userId(userId)
                .build();

        assert pairedThermostatRepository != null;
        return pairedThermostatRepository.save(pairedThermostat);
    }

    String createThing(String thermostatId){
        String thingName = "thermostat_" + thermostatId;
        String thingArn = awsIotService.createThing(thingName);

        CreateKeysAndCertificateResponse keysAndCert = awsIotService.createKeysAndCertificate();
        String certificateArn = keysAndCert.certificateArn();

        awsIotService.attachPolicy("RaspberryPiPolicy", certificateArn);
        awsIotService.attachThingPrincipal(thingName, certificateArn);

        // Store keysAndCert.certPem(), keysAndCert.keyPair().privateKey(), etc. securely

        return thingArn;
    }

    @Transactional
    public boolean unPairThermostat(String thermostatId, Long userId) throws Exception {
        assert userRepository != null;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        assert thermostatRepository != null;
        Thermostat thermostat = thermostatRepository.findById(UUID.fromString(thermostatId)).orElseThrow(
                () -> new RuntimeException("Thermostat not found")
        );

        assert pairedThermostatRepository != null;
        PairedThermostat pairedThermostat = pairedThermostatRepository.findByThermostatIdAndUserId(UUID.fromString(thermostatId), userId).orElseThrow(
                () -> new RuntimeException("Pairing not found")
        );

        thermostat.setIsPaired(false);
        thermostatRepository.save(thermostat);

        assert scheduleRepository != null;
        List<Schedule> schedules = scheduleRepository.findByThermostatIdAndUserId(UUID.fromString(thermostatId), userId);
        scheduleRepository.deleteAll(schedules);

        pairedThermostatRepository.delete(pairedThermostat);

        return true;
    }

}
