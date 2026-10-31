package com.example.car_rental_sys.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Enterprise Password Utility using jBCrypt.
 *
 * <p>Replaces obsolete, insecure MD5 hashing with salted, adaptive key stretching (Blowfish-based BCrypt).
 * Ensures compliance with modern security standards (OWASP / academic requirements).</p>
 */
public final class PasswordUtil {

    /**
     * Default log rounds (work factor) for BCrypt hashing.
     * 12 rounds is the current enterprise benchmark (4096 iterations).
     */
    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Prevent instantiation of utility class
    }

    /**
     * Hashes a plaintext password using BCrypt with a randomly generated salt.
     *
     * @param plainPassword the plaintext password to hash
     * @return the 60-character BCrypt hashed string
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String salt = BCrypt.gensalt(LOG_ROUNDS);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Verifies a candidate plaintext password against an existing BCrypt hash.
     *
     * @param plainPassword  the plaintext password provided by the user
     * @param hashedPassword the stored 60-character BCrypt hash
     * @return true if the password matches the hash; false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        if (!hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$") && !hashedPassword.startsWith("$2y$")) {
            return false; // Malformed or non-BCrypt hash
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
