package jewoospring.splearn.application.member.required;

import jewoospring.splearn.domain.shared.Email;
import jewoospring.splearn.domain.member.Member;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원 정보를 저장하거나 조회한다.
 */
public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByEmail(Email email);

    int deleteAll();

    void deleteAllInBatch();

    Optional<Member> findById(Long id);

    List<Member> findAll();
}
