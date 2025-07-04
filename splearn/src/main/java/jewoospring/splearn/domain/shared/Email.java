package jewoospring.splearn.domain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Email(
        @Column(name = "email_address", length = 50)
        String address
) {
    private static Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public Email {
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("이메일 형식이 바르지 않습니다: " + address);
        }

        if (address == null) {
            throw new RuntimeException("이메일 설정 관련해서 비이상적인 사용이 확인되었습니다");
        }
    }
}
