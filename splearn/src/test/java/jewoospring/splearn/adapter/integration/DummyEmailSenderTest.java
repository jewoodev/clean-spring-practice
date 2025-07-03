package jewoospring.splearn.adapter.integration;

import jewoospring.splearn.domain.Email;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import static org.assertj.core.api.Assertions.assertThat;

class DummyEmailSenderTest {
    @Test
    @StdIo
    void dummyEmailSender(StdOut out) {
        var dummyEmailSender = new DummyEmailSender();
        dummyEmailSender.send(new Email("jewoo15@example.com"), "subject", "body");

        assertThat(out.capturedLines()[0]).isEqualTo("DummyEmailSender send email: Email[address=jewoo15@example.com]");
    }
}