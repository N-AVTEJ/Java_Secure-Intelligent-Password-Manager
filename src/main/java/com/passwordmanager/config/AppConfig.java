package com.passwordmanager.config;

public final class AppConfig {
    private AppConfig() {
    }

    public static final String DB_URL = "jdbc:mysql://localhost:3306/password_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";

    public static final int PBKDF2_ITERATIONS = 65536;
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH = 128;
}
