package com.gimnasio.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encriptar(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean verificar(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}