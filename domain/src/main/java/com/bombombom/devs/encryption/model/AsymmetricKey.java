package com.bombombom.devs.encryption.model;

import com.bombombom.devs.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AsymmetricKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_key", nullable = false, length = 2048)
    private String publicKey;

    @Column(name = "private_key", nullable = false, length = 2048)
    private String privateKey;

    public static AsymmetricKey generate(String publicKey, String privateKey) {
        return AsymmetricKey.builder()
            .publicKey(publicKey)
            .privateKey(privateKey)
            .build();
    }
}
