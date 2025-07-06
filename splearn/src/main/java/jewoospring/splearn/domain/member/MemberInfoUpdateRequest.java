package jewoospring.splearn.domain.member;

import jakarta.validation.constraints.Size;

public record MemberInfoUpdateRequest(
        @Size(max = 20)
        String profileAddress,

        @Size(min = 5, max = 50)
        String introduction
) {
}
