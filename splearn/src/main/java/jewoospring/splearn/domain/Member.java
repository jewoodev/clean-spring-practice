package jewoospring.splearn.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.state;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {
    @NaturalId
    @Embedded
    private Email email;

    /** 닉네임 **/
    private String nickname;

    private String passwordHash;

    private MemberStatus status;

    public static Member register(MemberRegisterRequest createRequest, PasswordEncoder passwordEncoder) {
        var member = new Member();

        member.email = new Email(requireNonNull(createRequest.email()));
        member.nickname = requireNonNull(createRequest.nickname());
        member.passwordHash = requireNonNull(passwordEncoder.encode(createRequest.password()));

        member.status = MemberStatus.PENDING;

        return member;
    }

    public void activate() {
        state(status == MemberStatus.PENDING, "PENDING 상태가 아닙니다");

        this.status = MemberStatus.ACTIVE;
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다");

        this.status = MemberStatus.DEACTIVATED;
    }

    public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, passwordHash);
    }

    public void changeNickname(String nickname) {
        isTrue(nickname.length() >= 5 && nickname.length() <= 20, "닉네임은 5 ~ 20 자 사이의 길이를 가져야 합니다");
        this.nickname = nickname;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        isTrue(password.length() >= 8 && password.length() <= 100, "비밀번호는 8 ~ 100자 사이의 길이를 가져야 합니다");
        this.passwordHash = passwordEncoder.encode(password);
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
