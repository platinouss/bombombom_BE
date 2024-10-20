package com.bombombom.devs.external.global.decryption.dto;

public record EncryptedRequest(
    int id,
    long version,
    byte[] encryptedData
) {

}
