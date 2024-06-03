package thermolearn.backend.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thermolearn.backend.api.services.SecretsManagerService;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtils {

    private final String SECRET_KEY;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    @Autowired
    public EncryptionUtils(SecretsManagerService secretsManagerService) {
        this.SECRET_KEY = secretsManagerService.getSecretValue("ENCRYPTION_SECRET_KEY");
    }

    public String encrypt(String input) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

    public String decrypt(String input) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
