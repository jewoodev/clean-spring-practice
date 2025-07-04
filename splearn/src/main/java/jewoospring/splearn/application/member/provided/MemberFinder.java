package jewoospring.splearn.application.member.provided;

import jewoospring.splearn.domain.member.Member;

/**
 * 회원을 조회한다.
 */
public interface MemberFinder {
    Member find(Long memberId);
}
