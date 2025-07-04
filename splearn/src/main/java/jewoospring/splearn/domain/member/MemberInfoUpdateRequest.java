package jewoospring.splearn.domain.member;

public record MemberInfoUpdateRequest(
        String profileAddress,
        String introduction
) {
}
