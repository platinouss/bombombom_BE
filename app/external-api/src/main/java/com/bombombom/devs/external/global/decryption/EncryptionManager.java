package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.core.util.encryption.AESEncryption;
import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.core.util.encryption.RSAEncryption;
import com.bombombom.devs.core.util.encryption.SymmetricKeyEncryption;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EncryptionManager {

    @Bean
    public SymmetricKeyEncryption symmetricKeyEncryption() {
        return new AESEncryption();
    }

    @Bean
    public AsymmetricKeyEncryption asymmetricKeyEncryption() {
        return new RSAEncryption();
    }
}
