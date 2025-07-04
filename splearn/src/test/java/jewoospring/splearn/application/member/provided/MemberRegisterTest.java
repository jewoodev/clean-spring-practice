package jewoospring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.*;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static jewoospring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
record MemberRegisterTest(MemberRegister memberRegister, MemberRepository memberRepository, EntityManager em) {

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void register() {
        Member member = memberRegister.register(createMemberRegisterRequest());

        System.out.println(member);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailInFailure() {
        memberRegister.register(createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void activateSuccessful() {
        Member member = memberRegister.register(createMemberRegisterRequest());

        member = memberRegister.activate(member.getId());

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void memberRegisterRequestFail() {
        checkValidation(new MemberRegisterRequest("jewoos15@naver.com", "jeo", "longsecret"));
        checkValidation(new MemberRegisterRequest("jewoos15@naver.com", "HamonYe-----------------------------------------------------", "longsecret"));
        checkValidation(new MemberRegisterRequest("jewoos15naver.com", "HamonYe", "longsecret"));
        checkValidation(new MemberRegisterRequest("jewoos15naver.com", "HamonYe", "short"));
    }

    private AbstractThrowableAssert<?, ? extends Throwable> checkValidation(MemberRegisterRequest invalid) {
        return assertThatThrownBy(() -> memberRegister.register(invalid))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void deactivateSuccessful() {
        var member = memberRegister.register(createMemberRegisterRequest());

        member = memberRegister.activate(member.getId());
        member = memberRegister.deactivate(member.getId());

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }

    @Test
    void deactivateInFailure() {
        var member = memberRegister.register(createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.deactivate(member.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateInfoSuccessful() {
        var member = memberRegister.register(createMemberRegisterRequest());
        var updateRequest = new MemberInfoUpdateRequest("@jewoo", "저는 제우입니다.");

        member = memberRegister.activate(member.getId());

        member = memberRegister.updateInfo(member.getId(), updateRequest);

        assertThat(member.getDetail().getProfile().address()).isEqualTo(updateRequest.profileAddress());
        assertThat(member.getDetail().getIntroduction()).isEqualTo(updateRequest.introduction());
        assertThat(member.getDetail().getRegisteredAt()).isNotNull();
    }

    @Test
    void updateInfoInFailure() {
        var member = memberRegister.register(createMemberRegisterRequest());
        var updateRequest = new MemberInfoUpdateRequest("@jewoo", "저는 제우입니다.");

        var finalMember1 = member;
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember1.getId(), updateRequest))
                .isInstanceOf(IllegalStateException.class);

        member = memberRegister.activate(member.getId());

        var invalid1 = new MemberInfoUpdateRequest("j", "저는 제우입니다.");

        var finalMember2 = member;
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid1))
                .isInstanceOf(ConstraintViolationException.class);

        var invalid2 = new MemberInfoUpdateRequest("@jewoo", "앙!");
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid2))
                .isInstanceOf(ConstraintViolationException.class);

        var invalid3 = new MemberInfoUpdateRequest("@jewoo", null);
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid3))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
