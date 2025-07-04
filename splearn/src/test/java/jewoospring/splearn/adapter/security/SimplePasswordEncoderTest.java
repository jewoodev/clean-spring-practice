package jewoospring.splearn.adapter.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePasswordEncoderTest {
    @Test
    void encode() {
        var passwordEncoder = new SimplePasswordEncoder();

        passwordEncoder.encode("password");
    }

    @Test
    void matches() {
        var passwordEncoder = new SimplePasswordEncoder();
        String passwordHash = passwordEncoder.encode("password");

        assertThat(passwordEncoder.matches("password", passwordHash)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", passwordHash)).isFalse();
    }
}