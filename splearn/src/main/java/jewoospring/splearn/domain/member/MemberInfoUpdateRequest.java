package jewoospring.splearn.domain.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberInfoUpdateRequest(
        @NotNull
        Long memberId,

        @Size(max = 20)
        String profileAddress,

        @Size(min = 5, max = 50)
        String introduction
) {
}
