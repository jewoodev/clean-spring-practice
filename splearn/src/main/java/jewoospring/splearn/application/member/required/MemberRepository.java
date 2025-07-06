package jewoospring.splearn.application.member.required;

import jewoospring.splearn.domain.member.Member;
import jewoospring.splearn.domain.member.Profile;
import jewoospring.splearn.domain.shared.Email;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원 정보를 저장하거나 조회한다.
 */
public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findByEmail(Email email);

    void deleteAllInBatch();

    Optional<Member> findById(Long id);

    List<Member> findAll();

    @Query("SELECT EXISTS (SELECT m FROM Member m WHERE m.detail.profile = :profile)")
    boolean existsByProfile(Profile profile);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.detail.profile = :profile")
    int countByProfile(Profile profile);
}
