package com.bombombom.devs.external.encryption.controller;

import com.bombombom.devs.external.encryption.controller.dto.ProvidePublicKeyResponse;
import com.bombombom.devs.external.encryption.service.AsymmetricEncryptionService;
import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/encryption")
public class EncryptionController {

    private final AsymmetricEncryptionService asymmetricEncryptionService;

    @GetMapping("/public-key")
    public ResponseEntity<ProvidePublicKeyResponse> providePublicKey() {
        PublicKeyResult publicKeyResult = asymmetricEncryptionService.getRandomPublicKeyInfo();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ProvidePublicKeyResponse.fromResult(publicKeyResult));
    }
}
