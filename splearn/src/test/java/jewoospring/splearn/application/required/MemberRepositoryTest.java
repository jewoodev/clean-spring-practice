package jewoospring.splearn.application.required;

import jakarta.persistence.EntityManager;
import jewoospring.splearn.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static jewoospring.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static jewoospring.splearn.domain.MemberFixture.createPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void createMember() {
        var member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());

        assertThat(member.getId()).isNull();

        memberRepository.save(member);

        assertThat(member.getId()).isNotNull();

        em.flush();
    }
}