package jewoospring.splearn.application.member;

import jakarta.transaction.Transactional;
import jewoospring.splearn.application.member.provided.MemberFinder;
import jewoospring.splearn.application.member.provided.MemberRegister;
import jewoospring.splearn.application.member.required.EmailSender;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.*;
import jewoospring.splearn.domain.shared.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class MemberModifyService implements MemberRegister {
    private final MemberFinder memberFinder;
    private final MemberRepository memberRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Member register(MemberRegisterRequest registerRequest) {
        checkDuplicatedEmail(registerRequest);

        Member member = Member.register(registerRequest, passwordEncoder);

        memberRepository.save(member);

        sendWelcomeEmail(member);

        return member;
    }

    private void checkDuplicatedEmail(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByEmail(new Email(registerRequest.email())).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + registerRequest.email());
        }
    }

    private void sendWelcomeEmail(Member member) {
        emailSender.send(member.getEmail(), "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요");
    }

    @Transactional
    @Override
    public Member activate(Long memberId) {
        Member member = memberFinder.find(memberId);

        member.activate();

        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public Member deactivate(Long memberId) {
        Member member = memberFinder.find(memberId);

        member.deactivate();

        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public Member updateInfo(Long memberId, MemberInfoUpdateRequest updateRequest) {
        Member member = memberFinder.find(memberId);

        checkDuplicatedProfile(member, updateRequest.profileAddress());
        member.updateInfo(updateRequest);

        return memberRepository.save(member);
    }

    private void checkDuplicatedProfile(Member member, String profileAddress) {
        if (profileAddress.isEmpty()) return;

        Profile profile = member.getDetail().getProfile();
        // 처음 세팅하는게 아니고, 원래 프로필 주소와 같은 주소이면 그냥 return
        if (profile != null && profile.address().equals(profileAddress)) return;

        if (memberFinder.isExistProfile(profileAddress)) {
            throw new DuplicateProfileException("이미 사용 중인 프로필 주소입니다: " + profileAddress);
        }
    }
}
