package com.bombombom.devs.common.constant;

public class EncryptionRedisConstant {

    private static final String CURRENT_VERSION_SUFFIX = ":current_version";

    public static final String ASYMMETRIC_KEY_PREFIX = "asymmetric_key:";

    public static String getRedisKeyForAsymmetricKeyPair(int id, double version) {
        return ASYMMETRIC_KEY_PREFIX + id + ":" + version;
    }

    public static String getRedisKeyForAsymmetricKeyPairVersion(int id) {
        return ASYMMETRIC_KEY_PREFIX + id + CURRENT_VERSION_SUFFIX;
    }
}
