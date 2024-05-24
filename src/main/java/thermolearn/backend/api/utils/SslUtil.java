package thermolearn.backend.api.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class SslUtil {

    public static SSLSocketFactory getSocketFactory(final String crtFile, final String keyFile, final String password) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // Load client certificate
        X509Certificate cert = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(crtFile))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(bis);
        }

        // Load client private key
        PEMParser pemParser = new PEMParser(new FileReader(keyFile));
        Object object = pemParser.readObject();
        pemParser.close();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        KeyPair key;
        if (object instanceof PEMEncryptedKeyPair) {
            PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            key = converter.getKeyPair(ckp.decryptKeyPair(decProv));
        } else {
            key = converter.getKeyPair((PEMKeyPair) object);
        }

        // Client key and certificates are sent to server so it can authenticate us
        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        clientKeyStore.load(null, null);
        clientKeyStore.setCertificateEntry("certificate", cert);
        clientKeyStore.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, password.toCharArray());

        // Create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), null, new SecureRandom());

        return context.getSocketFactory();
    }
}
