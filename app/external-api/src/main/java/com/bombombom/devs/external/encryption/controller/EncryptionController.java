package com.bombombom.devs.external.encryption.controller;

import com.bombombom.devs.external.encryption.controller.dto.ProvidePublicKeyResponse;
import com.bombombom.devs.external.encryption.service.EncryptionService;
import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/encryption")
public class EncryptionController {

    private final EncryptionService encryptionService;

    @GetMapping("/public-key")
    public ResponseEntity<ProvidePublicKeyResponse> providePublicKey() {
        PublicKeyResult publicKeyResult = encryptionService.getRandomPublicKeyInfo();
        return ResponseEntity.ok(ProvidePublicKeyResponse.fromResult(publicKeyResult));
    }
}
