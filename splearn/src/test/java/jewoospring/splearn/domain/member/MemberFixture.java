package jewoospring.splearn.domain.member;

import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {
    public static Member createMember(Long memberId) {
        Member member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    public static MemberRegisterRequest createMemberRegisterRequest(String email) {
        return new MemberRegisterRequest(email, "jewoo", "secretsecretsecret");
    }

    public static MemberRegisterRequest createMemberRegisterRequest() {
        return createMemberRegisterRequest("jewoo15@example.com");
    }

    public static PasswordEncoder createPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase();
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return password.toUpperCase().equals(passwordHash);
            }
        };
    }
}
