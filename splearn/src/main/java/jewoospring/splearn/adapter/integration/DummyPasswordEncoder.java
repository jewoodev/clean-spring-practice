package jewoospring.splearn.adapter.integration;

import jewoospring.splearn.domain.PasswordEncoder;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Component
@Fallback
public class DummyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String password) {
        return password.toUpperCase();
    }

    @Override
    public boolean matches(String password, String passwordHash) {
        return passwordHash.equals(encode(password));
    }
}
