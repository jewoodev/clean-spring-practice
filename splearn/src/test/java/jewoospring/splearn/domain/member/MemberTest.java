package jewoospring.splearn.domain.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static jewoospring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static jewoospring.splearn.domain.member.MemberFixture.createPasswordEncoder;
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
        assertThat(member.getDetail().getRegisteredAt()).isNotNull();
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
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
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
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
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
        member.activate();

        assertThat(member.getNickname()).isEqualTo("jewoo");

        member.changeNickname("unhounho");

        assertThat(member.getNickname()).isEqualTo("unhounho");
    }

    @Test
    void changeNicknameInFailure() {
        assertThatThrownBy(() -> member.changeNickname("jewoo"))
                .isInstanceOf(IllegalStateException.class);

        member.activate();

        assertThat(member.getNickname()).isEqualTo("jewoo");

        assertThatThrownBy(() -> member.changeNickname("unho"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> member.changeNickname("unho-------------------------------------------------"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changePasswordSuccessful() {
        member.activate();

        member.changePassword("verysecret", passwordEncoder);

        assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
    }

    @Test
    void changePasswordInFailure() {
        assertThatThrownBy(() -> member.changePassword("short", passwordEncoder))
                .isInstanceOf(IllegalStateException.class);

        member.activate();

        assertThatThrownBy(() -> member.changePassword("short", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> member.changePassword("verysecretverysecretverysecretverysecretverysecretverysecretverysecretverysecretverysecretverysecretverysecretverysecret", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
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

    @Test
    void updateInfoSuccessful() {
        member.activate();

        var request = new MemberInfoUpdateRequest("@jewoo", "저는 제우입니다.");
        member.updateInfo(request);

        assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
        assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
    }

    @Test
    void updateInfoInFailure() {
        assertThatThrownBy(() -> member.updateInfo(new MemberInfoUpdateRequest(null, "저는 제우입니다.")))
                .isInstanceOf(IllegalStateException.class);

        member.activate();

        assertThatThrownBy(() -> member.updateInfo(new MemberInfoUpdateRequest(null, "저는 제우입니다.")))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> member.updateInfo(new MemberInfoUpdateRequest("@jewoo", "앙!")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}