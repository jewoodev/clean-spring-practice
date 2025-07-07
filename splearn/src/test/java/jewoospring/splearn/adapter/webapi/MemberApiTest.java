package jewoospring.splearn.adapter.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jewoospring.splearn.adapter.webapi.dto.MemberRegisterResponse;
import jewoospring.splearn.application.member.provided.MemberRegister;
import jewoospring.splearn.application.member.required.MemberRepository;
import jewoospring.splearn.domain.member.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.io.UnsupportedEncodingException;

import static jewoospring.splearn.AssertThatUtils.isEqualTo;
import static jewoospring.splearn.AssertThatUtils.notNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class MemberApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final MemberRepository memberRepository;
    final MemberRegister memberRegister;
    final EntityManager entityManager;

    @Test
    void register() throws JsonProcessingException, UnsupportedEncodingException {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.memberId", notNull())
                .hasPathSatisfying("$.email", isEqualTo(request.email()));

        MemberRegisterResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), MemberRegisterResponse.class);

        Member member = memberRepository.findById(response.memberId()).orElseThrow();


        assertThat(member.getEmail().address()).isEqualTo(request.email());
        assertThat(member.getNickname()).isEqualTo(request.nickname());
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicatedEmail() throws JsonProcessingException {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void duplicatedProfile() throws JsonProcessingException {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        memberRegister.activate(member.getId());

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(member.getId(), "@jewoo", "저는 제우입니다.");
        memberRegister.updateInfo(request.memberId(), request);
//        entityManager.flush();
//        entityManager.clear();

        Member problemMember = memberRegister.register(
                new MemberRegisterRequest("bhjjdsa@example.com", "bhjjdsa", "secretsecret")
        );

        MemberInfoUpdateRequest problemRequest = new MemberInfoUpdateRequest(problemMember.getId(), "@jewoo", "저는 제우입니다.");

        String requestJson = objectMapper.writeValueAsString(problemRequest);

        MvcTestResult result = mvcTester.patch().uri("/api/member/main")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT);
    }
}
