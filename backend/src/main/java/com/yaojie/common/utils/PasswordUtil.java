package com.yaojie.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordUtil {

    private static final PasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || !isBcryptHash(storedPassword)) {
            return false;
        }
        return BCRYPT.matches(rawPassword, storedPassword);
    }

    public static String encode(String rawPassword) {
        return BCRYPT.encode(rawPassword);
    }

    private static boolean isBcryptHash(String value) {
        return value.startsWith("$2a$")
            || value.startsWith("$2b$")
            || value.startsWith("$2y$");
    }
}
