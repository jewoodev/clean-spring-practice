package jewoospring.splearn.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @Size(min = 5, max = 20)
        String nickname,

        @Size(min = 8, max = 100)
        String password
) {
}
