package com.bombombom.devs.external.global.decryption.dto;

public record EncryptedRequest(
    int id,
    double version,
    byte[] encryptedData
) {

}
