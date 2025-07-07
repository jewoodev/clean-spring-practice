package jewoospring.splearn.application.member.provided;

import jakarta.validation.ConstraintViolationException;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.*;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static jewoospring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
record MemberRegisterTest(MemberRegister memberRegister, MemberRepository memberRepository) {

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
        Member member = memberRegister.register(createMemberRegisterRequest());

        member = memberRegister.activate(member.getId());
        member = memberRegister.deactivate(member.getId());

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }

    @Test
    void deactivateInFailure() {
        Member member = memberRegister.register(createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.deactivate(member.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateInfoSuccessful() {
        Member member1 = memberRegister.register(createMemberRegisterRequest());
        var updateRequest1 = new MemberInfoUpdateRequest(member1.getId(), "@jewoo", "저는 제우입니다.");

        member1 = memberRegister.activate(member1.getId());

        member1 = memberRegister.updateInfo(member1.getId(), updateRequest1);

        assertThat(member1.getDetail().getProfile().address()).isEqualTo(updateRequest1.profileAddress());
        assertThat(member1.getDetail().getIntroduction()).isEqualTo(updateRequest1.introduction());
        assertThat(member1.getDetail().getRegisteredAt()).isNotNull();

        Member member2 = memberRegister.register(createMemberRegisterRequest("unhoyouknow@example.com"));
        var updateRequest2 = new MemberInfoUpdateRequest(member2.getId(), "@unhoyouknow", "저는 유노윤호입니다.");

        member2 = memberRegister.activate(member2.getId());

        member2 = memberRegister.updateInfo(member2.getId(), updateRequest2);

        assertThat(member2.getDetail().getProfile().address()).isEqualTo(updateRequest2.profileAddress());
        assertThat(member2.getDetail().getIntroduction()).isEqualTo(updateRequest2.introduction());
        assertThat(member2.getDetail().getActivatedAt()).isNotNull();

        // 프로필 주소를 제거하는 것도 가능하다.
        var removeProfile = new MemberInfoUpdateRequest(member2.getId(), "", "저는 유노윤호입니다.");
        member2 = memberRegister.updateInfo(member2.getId(), removeProfile);
        assertThat(member2.getDetail().getProfile().address()).isEqualTo("");
    }

    @Test
    void updateInfoInFailure() {
        Member member = memberRegister.register(createMemberRegisterRequest());
        var validRequest = new MemberInfoUpdateRequest(member.getId(), "@jewoo", "저는 제우입니다.");

        var finalMember1 = member;
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember1.getId(), validRequest))
                .isInstanceOf(IllegalStateException.class);

        member = memberRegister.activate(member.getId());

        var finalMember2 = member;
        // 프로필 주소는 맨 앞에 @가 없으면 실패한다.
        var invalid1 = new MemberInfoUpdateRequest(finalMember2.getId(), "j", "저는 제우입니다.");
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid1))
                .isInstanceOf(IllegalArgumentException.class);

        // 자기 소개 글은 다섯 글자 보다 짧을 수 없다.
        var invalid2 = new MemberInfoUpdateRequest(finalMember2.getId(), "@jewoo", "앙!");
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid2))
                .isInstanceOf(ConstraintViolationException.class);

        // 프로필 주소이건 자기 소개 글이건 null값으로 변경하는 요청은 비정상적인 요청이다.
        var invalid3 = new MemberInfoUpdateRequest(finalMember2.getId(), "@jewoo", null);
        assertThatThrownBy(() -> memberRegister.updateInfo(finalMember2.getId(), invalid3))
                .isInstanceOf(NullPointerException.class);

        memberRegister.updateInfo(member.getId(), validRequest);

        // mdp = memberDuplicateProfile
        Member mdp = memberRegister.register(new MemberRegisterRequest("mongodb@example.com", "mongodb", "mongomongo")); // 프로필 주소 중복될 멤버, memberDuplicateProfile

        memberRegister.activate(mdp.getId());

        // urdp = updateRequestDuplicateProfile
        var urdp = new MemberInfoUpdateRequest(mdp.getId(), "@jewoo", "저는 제우입니다.");

        assertThatThrownBy(() -> memberRegister.updateInfo(mdp.getId(), urdp))
                .isInstanceOf(DuplicateProfileException.class);
    }
}
