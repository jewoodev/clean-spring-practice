package jewoospring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static jewoospring.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static jewoospring.splearn.domain.MemberFixture.createPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {
    private Member member;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = createPasswordEncoder();

        var createRequest = createMemberRegisterRequest();
        member = Member.register(createRequest, passwordEncoder);
    }

    @Test
    void registerMember() {
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void registerNullCheck() {
        var createRequest = createMemberRegisterRequest(null);
        assertThatThrownBy(() -> Member.register(createRequest, passwordEncoder))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void activateSuccessful() {
        member.activate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void activateInFailure() {
        member.activate();

        assertThatThrownBy(() -> member.activate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deactivateSuccessful() {
        member.activate();

        member.deactivate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @Test
    void deactivateInFailure() {
        assertThatThrownBy(() -> member.deactivate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void passwordVerifySuccessful() {
        assertThat(member.verifyPassword("secretsecretsecret", passwordEncoder)).isTrue();
    }

    @Test
    void passwordVerifyInFailure() {
        assertThat(member.verifyPassword("wrongPassword", passwordEncoder)).isFalse();
    }

    @Test
    void changeNicknameSuccessful() {
        assertThat(member.getNickname()).isEqualTo("jewoo");

        member.changeNickname("unho");

        assertThat(member.getNickname()).isEqualTo("unho");
    }

    @Test
    void changePasswordSuccessful() {
        member.changePassword("verysecret", passwordEncoder);

        assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
    }

    @Test
    void isActive() {
        assertThat(member.isActive()).isFalse();

        member.activate();

        assertThat(member.isActive()).isTrue();

        member.deactivate();

        assertThat(member.isActive()).isFalse();
    }

    @Test
    void invalidEmail() {
        assertThatThrownBy(() ->
                Member.register(
                        createMemberRegisterRequest("invalidEmail"),
                        passwordEncoder)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(
                Member.register(createMemberRegisterRequest(), passwordEncoder)
        ).isInstanceOf(Member.class);
    }
}