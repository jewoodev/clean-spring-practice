package jewoospring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {
    private Member member;

    private static String password = "testPasswordHash";

    private static PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(String password) {
            return password.toUpperCase();
        }

        @Override
        public boolean matches(String password, String passwordHash) {
            return password.toUpperCase().equals(passwordHash);
        }
    };

    @BeforeEach
    void setUp() {
        member = Member.create("jewoo15@example.com", "jewoo", password, passwordEncoder);
    }

    @DisplayName("회원이 최초로 생성된 시점의 상태는 '대기중' 상태이다.")
    @Test
    void createMember() {
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @DisplayName("회원은 '대기중' 상태이면 '활성 상태' 로 변환이 가능하다.")
    @Test
    void activateSuccessful() {
        member.activate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @DisplayName("회원은 한 번 '활성 상태'가 되고나면 더이상 '활성 상태'로의 변환이 불가능하다.")
    @Test
    void activateInFailure() {
        member.activate();

        assertThatThrownBy(() -> member.activate())
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("회원은 '활성' 상태일 때 '탈퇴' 상태로 전환될 수 있다.")
    @Test
    void deactivateSuccessful() {
        member.activate();

        member.deactivate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @DisplayName("회원은 '대기중' 상태일 때 '탈퇴' 상태로 전환될 수 없다.")
    @Test
    void deactivateInFailure() {
        assertThatThrownBy(() -> member.deactivate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void passwordMatchesSuccessful() {
        assertThat(passwordEncoder.matches(password, member.getPasswordHash())).isTrue();
    }

    @Test
    void passwordMatchesInFailure() {
        assertThat(passwordEncoder.matches("wrongPasswordHash", member.getPasswordHash())).isFalse();
    }
}