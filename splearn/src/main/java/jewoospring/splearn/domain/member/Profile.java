package jewoospring.splearn.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Profile(
        @Column(length = 20, name = "profile_address")
        String address
) {
    private static final Pattern PROFILE_ADDRESS_PATTERN = Pattern.compile("^@[a-z0-9]+");

    public Profile {
        if (address == null) {
            throw new NullPointerException("프로필 설정에 관련된 비이상적인 사용이 확인되었습니다.");
        }

        if (!address.isEmpty() && !PROFILE_ADDRESS_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("프로필 주소 형식이 올바르지 않습니다: " + address);
        }

        if (address.length() > 21) {
            throw new IllegalArgumentException("프로필 주소는 최대 20자리를 넘을 수 없습니다");
        }
    }
}
