package com.bombombom.devs.external.encryption;

import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.core.util.encryption.RSAEncryption;
import com.bombombom.devs.external.encryption.service.EncryptionService;
import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptionTest {

    @Autowired
    private EncryptionService encryptionService;

    private final AsymmetricKeyEncryption asymmetricKeyEncryption = new RSAEncryption();

    @Test
    @DisplayName("RSA 암복호화 테스트")
    void test() throws Exception {
        String data = "RSA 암복호화 테스트입니다.";
        PublicKeyResult publicKeyResult = encryptionService.getRandomPublicKeyInfo();
        int publicKeyId = publicKeyResult.id();
        double publicKeyVersion = publicKeyResult.version();
        PublicKey publicKey = asymmetricKeyEncryption.deserializePublicKey(
            publicKeyResult.publicKey());
        byte[] encryptedData = asymmetricKeyEncryption.encrypt(
            data.getBytes(StandardCharsets.UTF_8), publicKey);
        String decryptedData = encryptionService.decryptData(publicKeyId, publicKeyVersion,
            encryptedData);
        Assertions.assertEquals(data, decryptedData);
    }

}
