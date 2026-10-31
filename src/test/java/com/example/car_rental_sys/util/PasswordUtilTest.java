package com.example.car_rental_sys.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying jBCrypt password hashing security and verification.
 */
class PasswordUtilTest {

    @Test
    @DisplayName("Should successfully hash password and generate standard 60-character BCrypt string")
    void shouldHashPasswordSuccessfully() {
        String rawPassword = "SecurePassword@123";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertNotNull(hash);
        assertEquals(60, hash.length(), "BCrypt hash must be exactly 60 characters");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "BCrypt prefix expected");
    }

    @Test
    @DisplayName("Should verify matching password correctly")
    void shouldVerifyMatchingPassword() {
        String rawPassword = "AdminSecret#2026";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertTrue(PasswordUtil.verifyPassword(rawPassword, hash), "Password verification must succeed for valid password");
    }

    @Test
    @DisplayName("Should reject invalid or mismatched passwords")
    void shouldRejectInvalidPassword() {
        String rawPassword = "CorrectPassword";
        String wrongPassword = "WrongPassword";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertFalse(PasswordUtil.verifyPassword(wrongPassword, hash), "Verification must fail for wrong password");
    }

    @Test
    @DisplayName("Should generate different salts for identical raw passwords (salt randomization)")
    void shouldGenerateUniqueSaltsForIdenticalPasswords() {
        String rawPassword = "SamePassword123";
        String hash1 = PasswordUtil.hashPassword(rawPassword);
        String hash2 = PasswordUtil.hashPassword(rawPassword);

        assertNotEquals(hash1, hash2, "BCrypt must salt each hash independently to prevent rainbow table attacks");
        assertTrue(PasswordUtil.verifyPassword(rawPassword, hash1));
        assertTrue(PasswordUtil.verifyPassword(rawPassword, hash2));
    }

    @Test
    @DisplayName("Should handle null or empty inputs gracefully without crashing")
    void shouldHandleEdgeCasesSafely() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));

        assertFalse(PasswordUtil.verifyPassword(null, "someHash"));
        assertFalse(PasswordUtil.verifyPassword("password", null));
        assertFalse(PasswordUtil.verifyPassword("password", "invalidHashFormat"));
    }
}
