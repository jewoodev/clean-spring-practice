package jewoospring.splearn.application;

import jewoospring.splearn.application.provided.MemberRegister;
import jewoospring.splearn.application.required.EmailSender;
import jewoospring.splearn.application.required.MemberRepository;
import jewoospring.splearn.domain.Member;
import jewoospring.splearn.domain.MemberRegisterRequest;
import jewoospring.splearn.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberRegister {
    private final MemberRepository memberRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member register(MemberRegisterRequest registerRequest) {
        // check

        Member member = Member.register(registerRequest, passwordEncoder);

        memberRepository.save(member);

        emailSender.send(member.getEmail(), "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요");

        return member;
    }
}
