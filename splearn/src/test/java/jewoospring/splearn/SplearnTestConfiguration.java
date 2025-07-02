package jewoospring.splearn;

import jewoospring.splearn.application.required.EmailSender;
import jewoospring.splearn.domain.PasswordEncoder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SplearnTestConfiguration {
    @Bean
    public EmailSender emailSender() {
        return (email, subject, body) -> System.out.println("Sending email to " + email + " with subject " + subject + " and body " + body + "");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password;
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return passwordEncoder().matches(password, passwordHash);
            }
        };
    }
}
