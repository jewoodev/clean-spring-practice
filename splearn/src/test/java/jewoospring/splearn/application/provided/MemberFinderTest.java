package jewoospring.splearn.application.provided;

import jewoospring.splearn.SplearnTestConfiguration;
import jewoospring.splearn.application.required.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static jewoospring.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(SplearnTestConfiguration.class)
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
        assertThat(memberFinder.find(1L)).isNotNull();
    }

    @Test
    void findInFailure() {
        assertThatThrownBy(() -> memberFinder.find(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}