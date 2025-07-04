package jewoospring.splearn.adapter.security;

import jewoospring.splearn.domain.member.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Fallback;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Fallback
@RequiredArgsConstructor
public class SimplePasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String password, String passwordHash) {
        return passwordEncoder.matches(password, passwordHash);
    }
}
