package com.yaojie.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        return rawPassword.equals(storedPassword) || BCRYPT.matches(rawPassword, storedPassword);
    }

    public static String encode(String rawPassword) {
        return BCRYPT.encode(rawPassword);
    }
}
