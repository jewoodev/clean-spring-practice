package jewoospring.splearn.adapter.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DummyPasswordEncoderTest {
    @Test
    void encodeSuccessful() {
        var passwordEncoder = new DummyPasswordEncoder();

        passwordEncoder.encode("password");
    }

    @Test
    void matchesSuccessful() {
        var passwordEncoder = new DummyPasswordEncoder();
        String encoded = passwordEncoder.encode("password");

        passwordEncoder.matches("password", encoded);
    }
}