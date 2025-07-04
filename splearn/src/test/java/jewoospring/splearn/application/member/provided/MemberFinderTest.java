package jewoospring.splearn.application.member.provided;

import jewoospring.splearn.application.member.required.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static jewoospring.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
record MemberFinderTest(MemberRegister memberRegister, MemberFinder memberFinder, MemberRepository memberRepository) {
    @BeforeEach
    void setUp() {
        memberRegister.register(createMemberRegisterRequest());
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    void findSuccessful() {
        assertThat(memberFinder.findAll()).isNotNull();
    }

    @Test
    void findInFailure() {
        assertThatThrownBy(() -> memberFinder.find(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}