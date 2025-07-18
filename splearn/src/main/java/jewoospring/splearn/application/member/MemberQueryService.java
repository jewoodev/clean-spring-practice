package jewoospring.splearn.application.member;

import jewoospring.splearn.application.member.provided.MemberFinder;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.Member;
import jewoospring.splearn.domain.member.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class MemberQueryService implements MemberFinder {
    private final MemberRepository memberRepository;

    @Override
    public Member find(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id: " + memberId));
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public boolean isExistProfile(String profileAddress) {
        return memberRepository.existsByProfile(new Profile(profileAddress));
    }
}
