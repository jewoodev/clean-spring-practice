package jewoospring.splearn.application.provided;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jewoospring.splearn.SplearnTestConfiguration;
import jewoospring.splearn.application.required.MemberRepository;
import jewoospring.splearn.domain.*;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, MemberRepository memberRepository, EntityManager em) {

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailInFailure() {
        Member member1 = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void activateSuccessful() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

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
