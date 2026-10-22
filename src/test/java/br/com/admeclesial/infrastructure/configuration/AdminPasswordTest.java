package br.com.admeclesial.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminPasswordTest {
    @Test
    void testBcryptAdmin123() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");
        System.out.println("BCRYPT_HASH=" + encoded);
        assertTrue(encoder.matches("admin123", encoded));
    }
}
