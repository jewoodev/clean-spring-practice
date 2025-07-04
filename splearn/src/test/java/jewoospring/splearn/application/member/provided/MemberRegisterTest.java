package jewoospring.splearn.application.member.provided;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.DuplicateEmailException;
import jewoospring.splearn.domain.member.Member;
import jewoospring.splearn.domain.member.MemberRegisterRequest;
import jewoospring.splearn.domain.member.MemberStatus;
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
        Member member1 = memberRegister.register(createMemberRegisterRequest());

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
}
