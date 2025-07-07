package jewoospring.splearn.adapter.webapi;

import jakarta.validation.Valid;
import jewoospring.splearn.adapter.webapi.dto.MemberRegisterResponse;
import jewoospring.splearn.application.member.provided.MemberRegister;
import jewoospring.splearn.domain.member.Member;
import jewoospring.splearn.domain.member.MemberInfoUpdateRequest;
import jewoospring.splearn.domain.member.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {
    private final MemberRegister memberRegister;

    @PostMapping("/api/members")
    public MemberRegisterResponse register(@RequestBody @Valid MemberRegisterRequest request) {
        Member member =  memberRegister.register(request);

        return MemberRegisterResponse.of(member);
    }

    @PatchMapping("/api/member/main")
    public Member updateMainInfo(@RequestBody @Valid MemberInfoUpdateRequest request) {
        return memberRegister.updateInfo(request.memberId(), request);
    }
}
